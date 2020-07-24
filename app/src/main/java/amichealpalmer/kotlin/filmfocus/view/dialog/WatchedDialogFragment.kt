package amichealpalmer.kotlin.filmfocus.view.dialog

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.model.entity.TIMELINE_ITEM_STATUS
import amichealpalmer.kotlin.filmfocus.model.entity.TimelineItem
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
import org.joda.time.LocalDate

// Dialog fragment called when a film is marked watched in the context menu
class WatchedDialogFragment : DialogFragment(), RatingBar.OnRatingBarChangeListener, CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private val TAG = "WatchedDialogFragment"
    private lateinit var callback: OnWatchedDialogSubmissionListener
    private lateinit var film: FilmThumbnail
    private var rating: Float? = null
    private var hasRating = false
    private var status: TIMELINE_ITEM_STATUS = TIMELINE_ITEM_STATUS.Watched

    interface OnWatchedDialogSubmissionListener {
        fun onWatchedDialogSubmissionListener(timelineItem: TimelineItem)
    }

    fun setOnWatchedDialogSubmissionListener(callback: OnWatchedDialogSubmissionListener) {
        this.callback = callback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            film = requireArguments().getParcelable<FilmThumbnail>("film") as FilmThumbnail
            Log.d(TAG, ".onCreate: film is ${film.title}")
        } catch (e: NullPointerException) {
            Log.e(TAG, ".onCreate - failed to retrieve film from bundle")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? { // Called because we're using a custom XML layout to define the dialog layout
        // Inflate dialog's xml
        isCancelable = true
        return inflater.inflate(R.layout.fragment_watchlist_watched_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { // Called after onCreateView
        super.onViewCreated(view, savedInstanceState)

        fragment_watchlist_watched_dialog_ratingBar.onRatingBarChangeListener = this
        fragment_watchlist_watched_dialog_toggleWatched.setOnCheckedChangeListener(this)
        fragment_watchlist_watched_dialog_cancelButton.setOnClickListener(this)
        fragment_watchlist_watched_dialog_doneButton.setOnClickListener(this)

        Picasso.get().load(film.posterURL).error(R.drawable.ic_image_loading_grey_48dp)
                .placeholder(R.drawable.ic_image_loading_grey_48dp).into(fragment_watchlist_watched_dialog_poster_iv)

        // ?
        dialog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
        this.rating = rating
        hasRating = true
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            fragment_watchlist_watched_dialog_cancelButton.id -> this.dismiss()
            fragment_watchlist_watched_dialog_doneButton.id -> {
                // We send all the info to the Watchlist Fragment as a timeline item
                val date = LocalDate.now()
                val text = fragment_watchlist_watched_dialog_review_et.text.toString()
                val item = TimelineItem(film, rating ?: 0f, date, text, status)
                callback.onWatchedDialogSubmissionListener(item)
                this.dismiss()
            }
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (buttonView == fragment_watchlist_watched_dialog_toggleWatched) {
            var value = TIMELINE_ITEM_STATUS.Watched
            when (isChecked) {
                true -> value = TIMELINE_ITEM_STATUS.Watched
                false -> value = TIMELINE_ITEM_STATUS.Dropped
            }
            status = value
        }
    }

    companion object {

        const val TAG = "WatchedDialogFragment"

        fun newInstance(filmThumbnail: FilmThumbnail): WatchedDialogFragment {
            val fragment = WatchedDialogFragment()
            val bundle = Bundle()
            bundle.putParcelable("film", filmThumbnail)
            fragment.arguments = bundle
            return fragment
        }
    }

}