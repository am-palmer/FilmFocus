package amichealpalmer.kotlin.filmfocus.fragments

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.data.*
import amichealpalmer.kotlin.filmfocus.data.FilmRating
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.squareup.picasso.Picasso
import org.joda.time.LocalDate
import java.lang.NullPointerException

// Dialog fragment called when a film is marked watched in the context menu
class WatchedDialogFragment : DialogFragment(), RatingBar.OnRatingBarChangeListener, CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    // todo: support for dropped films
    private val TAG = "WatchedDialogFragment"

    private lateinit var callback: onWatchedDialogSubmissionListener

    private lateinit var poster: ImageView
    private lateinit var ratingBar: RatingBar
    private lateinit var reviewEditText: EditText
    private lateinit var toggleWatched: ToggleButton
    private lateinit var cancelButton: Button
    private lateinit var doneButton: Button
    private lateinit var film: FilmThumbnail

    private var rating: Float? = null
    private var hasRating: Boolean = false
    private var status: TIMELINE_ITEM_STATUS = TIMELINE_ITEM_STATUS.WATCHED

    interface onWatchedDialogSubmissionListener {
        fun onWatchedDialogSubmissionListener(timelineItem: TimelineItem)
    }

    fun setOnWatchedDialogSubmissionListener(callback: onWatchedDialogSubmissionListener) {
        this.callback = callback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            film = arguments!!.getParcelable<FilmThumbnail>("film") as FilmThumbnail
            Log.d(TAG, ".onCreate: film is ${film.title}")
        } catch (e: NullPointerException) {
            // todo: less general catch
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

        // todo: synthetic imports?
        poster = view.findViewById(R.id.fragment_watchlist_watched_dialog_poster_iv)
        ratingBar = view.findViewById(R.id.fragment_watchlist_watched_dialog_ratingBar)
        reviewEditText = view.findViewById(R.id.fragment_watchlist_watched_dialog_review_et)
        toggleWatched = view.findViewById(R.id.fragment_watchlist_watched_dialog_toggleWatched)
        cancelButton = view.findViewById(R.id.fragment_watchlist_watched_dialog_cancelButton)
        doneButton = view.findViewById(R.id.fragment_watchlist_watched_dialog_doneButton)

        ratingBar.onRatingBarChangeListener = this
        toggleWatched.setOnCheckedChangeListener(this)
        cancelButton.setOnClickListener(this)
        doneButton.setOnClickListener(this)

        Picasso.get().load(film.posterURL).error(R.drawable.placeholder_imageloading)
                .placeholder(R.drawable.placeholder_imageloading).into(poster)

        // ?
        dialog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
        this.rating = rating
        hasRating = true
    }

    override fun onClick(v: View?) {
        Log.d(TAG, ".onClick triggered")
        if (v?.id == cancelButton.id) {
            Log.d(TAG, "Cancel button clicked")
            // Close the dialog
            this.dismiss()
        } else if (v?.id == doneButton.id) {
            Log.d(TAG, "Done button clicked")
            // We send all the info to the Watchlist Fragment as a timeline item
            val date = LocalDate.now()
            val text = reviewEditText.text.toString()
            var ratingObject: FilmRating?
            if (hasRating){
                ratingObject = FilmRating(rating!!.toFloat(), RATING_VALUE.HAS_RATING)
            } else {
                ratingObject = FilmRating(0f, RATING_VALUE.NO_RATING)
            }
            val item = TimelineItem(film, ratingObject, date, text, status)
            callback.onWatchedDialogSubmissionListener(item)
            this.dismiss()
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (buttonView == toggleWatched) {
            var value = TIMELINE_ITEM_STATUS.WATCHED
            when (isChecked) {
                true -> value = TIMELINE_ITEM_STATUS.WATCHED
                false -> value = TIMELINE_ITEM_STATUS.DROPPED
            }
            status = value
        }
    }

    companion object {

        fun newInstance(filmThumbnail: FilmThumbnail): WatchedDialogFragment {
            val fragment = WatchedDialogFragment()
            val bundle = Bundle()
            bundle.putParcelable("film", filmThumbnail)
            fragment.arguments = bundle
            return fragment
        }
    }

}