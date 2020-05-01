package amichealpalmer.kotlin.filmfocus.view.dialog

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.model.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.CompoundButton
import android.widget.RatingBar
import androidx.fragment.app.DialogFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_watchlist_watched_dialog.*

// todo: code duplication with watchedDialogFragment - should they inherit from a parent class?

// This is the fragment that is displayed when a user edits an item in the history
class EditHistoryItemDialogFragment : DialogFragment(), RatingBar.OnRatingBarChangeListener, CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private val TAG = "EditHistoryItemDiaFrag"
    private lateinit var callback: onHistoryEditDialogSubmissionListener
    private var rating: Float? = null
    private var hasRating = false
    private lateinit var timelineItem: TimelineItem
    private lateinit var status: TIMELINE_ITEM_STATUS
    private var arrayPosition = 0

    interface onHistoryEditDialogSubmissionListener {
        fun onEditHistoryItemDialogSubmissionListener(timelineItem: TimelineItem, arrayPosition: Int)
    }

    fun setHistoryEditDialogSubmissionListener(callback: onHistoryEditDialogSubmissionListener) {
        this.callback = callback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            timelineItem = arguments!!.getParcelable<FilmThumbnail>("timelineItem") as TimelineItem
            arrayPosition = arguments!!.getInt("arrayPosition")
        } catch (e: NullPointerException) {
            Log.e(TAG, ".onCreate - failed to retrieve timelineItem from bundle")
        }
        rating = timelineItem.rating.value
        if (rating!!.toFloat() > 0f) {
            hasRating = true
        } else {
            rating = 0f
        }
        status = timelineItem.status

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? { // Called because we're using a custom XML layout to define the dialog layout
        // Inflate dialog's xml
        isCancelable = true
        return inflater.inflate(R.layout.fragment_watchlist_watched_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { // Called after onCreateView
        super.onViewCreated(view, savedInstanceState)

        // Load content from the timeline item
        fragment_watchlist_watched_dialog_ratingBar.rating = rating!!
        when (status){
            TIMELINE_ITEM_STATUS.DROPPED -> fragment_watchlist_watched_dialog_toggleWatched.isChecked = false
            TIMELINE_ITEM_STATUS.WATCHED -> fragment_watchlist_watched_dialog_toggleWatched.isChecked = true
        }
        fragment_watchlist_watched_dialog_review_et.setText(timelineItem.getReview())

        fragment_watchlist_watched_dialog_ratingBar.onRatingBarChangeListener = this
        fragment_watchlist_watched_dialog_toggleWatched.setOnCheckedChangeListener(this)
        fragment_watchlist_watched_dialog_cancelButton.setOnClickListener(this)
        fragment_watchlist_watched_dialog_doneButton.setOnClickListener(this)

        fragment_watchlist_watched_dialog_title.setText(R.string.edit_review)

        Picasso.get().load(timelineItem.film.posterURL).error(R.drawable.ic_image_loading_darkgreen_48dp)
                .placeholder(R.drawable.ic_image_loading_darkgreen_48dp).into(fragment_watchlist_watched_dialog_poster_iv)

        // ?
        dialog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
        this.rating = rating
        hasRating = true
    }

    override fun onClick(v: View?) {
        Log.d(TAG, ".onClick triggered")
        when (v?.id) {
            fragment_watchlist_watched_dialog_cancelButton.id -> this.dismiss()
            fragment_watchlist_watched_dialog_doneButton.id -> {
                Log.d(TAG, "Done button clicked")
                // We send all the info to the Watchlist Fragment as a timeline item
                val date = timelineItem.date
                val text = fragment_watchlist_watched_dialog_review_et.text.toString()
                val ratingObject: FilmRating?
                if (hasRating) {
                    ratingObject = FilmRating(rating!!.toFloat(), FILM_RATING_VALUE.HAS_RATING)
                } else {
                    ratingObject = FilmRating(0f, FILM_RATING_VALUE.NO_RATING)
                }
                val item = TimelineItem(timelineItem.film, ratingObject, date, text, status)
                callback.onEditHistoryItemDialogSubmissionListener(item, arrayPosition)
                this.dismiss()
            }
            else -> true
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (buttonView == fragment_watchlist_watched_dialog_toggleWatched) {
            var value = TIMELINE_ITEM_STATUS.WATCHED
            when (isChecked) {
                true -> value = TIMELINE_ITEM_STATUS.WATCHED
                false -> value = TIMELINE_ITEM_STATUS.DROPPED
            }
            status = value
        }
    }

    companion object {

        const val TAG = "EditHistoryItemDiaFrag"

        fun newInstance(timelineItem: TimelineItem, arrayPosition: Int): EditHistoryItemDialogFragment {
            val fragment = EditHistoryItemDialogFragment()
            val bundle = Bundle()
            bundle.putParcelable("timelineItem", timelineItem)
            bundle.putInt("arrayPosition", arrayPosition)
            fragment.arguments = bundle
            return fragment
        }
    }

}