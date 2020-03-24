package amichealpalmer.kotlin.filmfocus.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.activities.MainActivity
import amichealpalmer.kotlin.filmfocus.adapters.WatchlistRecyclerAdapter
import amichealpalmer.kotlin.filmfocus.data.Film
import amichealpalmer.kotlin.filmfocus.data.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.data.TIMELINE_ITEM_STATUS
import amichealpalmer.kotlin.filmfocus.data.TimelineItem
import android.app.Dialog
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_film_details.*
import kotlinx.android.synthetic.main.fragment_watchlist_watched_dialog.*
import org.joda.time.LocalDate
import java.lang.Exception
import java.lang.NullPointerException

private const val ARG_LIST = "watchlist"

enum class WATCHLIST_FILM_CONTEXT_ACTION_TYPE { // todo: Should this be somewhere else?
    WATCHLIST_REMOVE, WATCHLIST_MARK_WATCHED
}

class WatchlistFragment : Fragment(), WatchedDialogFragment.onWatchedDialogSubmissionListener { // note: code duplication with browsefragment. possibly have browsefragment and searchfragment/watchlistfragment subclasses todo: minimize duplication

    private val TAG = "WatchlistFragment"
    internal var callback: OnFilmSelectedListener? = null
    private lateinit var watchlist: ArrayList<FilmThumbnail>
    lateinit var recyclerView: RecyclerView

    fun setOnFilmSelectedListener(callback: OnFilmSelectedListener) {
        this.callback = callback
    }

    interface OnFilmSelectedListener {
        fun onFilmSelected(bundle: Bundle, typeWATCHLIST: WATCHLIST_FILM_CONTEXT_ACTION_TYPE)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        if (arguments != null) {
            watchlist = arguments!!.getParcelableArrayList<FilmThumbnail>(ARG_LIST) as ArrayList<FilmThumbnail>
        } else {
            Log.d(TAG, ".onCreateView: arguments null")
        }
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        Log.d(TAG, ".onCreateView called")
        var view = inflater.inflate(R.layout.fragment_browse, container, false)
        recyclerView = view.findViewById<RecyclerView>(R.id.browse_films_recyclerview_id)
        recyclerView.layoutManager = GridLayoutManager(activity, 3)
        recyclerView.adapter = WatchlistRecyclerAdapter(activity!!, watchlist)
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFilmSelectedListener) {
            callback = context
        } else {
            throw RuntimeException(context.toString() + " must implement onFilmSelectedListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Log.d(TAG, ".onCreateOptionsMenu called")
        inflater.inflate(R.menu.browse_fragment_menu, menu)

        val searchView = SearchView((context as MainActivity).supportActionBar?.themedContext
                ?: context)
        searchView.isIconifiedByDefault = false
        searchView.requestFocus()
        menu.findItem(R.id.browse_fragment_search).apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItem.SHOW_AS_ACTION_IF_ROOM)
            actionView = searchView
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // the search button will do nothing, so we should probably disable/hide it in the watchlist
                onQueryTextChange(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // Use the adapter filter to update the view
                val adapter = recyclerView.adapter as WatchlistRecyclerAdapter
                adapter.filter.filter(newText)
                return true
            }
        })
        searchView.setOnClickListener { view -> }

        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, ".onContextItemSelected called")
        val adapter = recyclerView.adapter as WatchlistRecyclerAdapter
        var position = -1
        try {
            position = adapter.position
        } catch (e: java.lang.Exception) { // todo: too general
            Log.d(TAG, e.localizedMessage, e)
            return super.onContextItemSelected(item)
        }
        when (item.itemId) {
            R.id.film_thumbnail_context_menu_mark_watched -> {
                val film = adapter.getItem(position)

                // todo: prompt user for review and rating properly
                val dialogFragment = WatchedDialogFragment.newInstance(film)
                dialogFragment.setOnWatchedDialogSubmissionListener(this)
                dialogFragment.show(fragmentManager!!, "fragment_watched_dialog")

            }
            R.id.film_thumbnail_context_menu_remove -> {
                val film = adapter.getItem(position)
                watchlist.remove(film)
                adapter.removeFilmFromWatchlist(film)
                val bundle = Bundle()
                bundle.putParcelable("film", film)
                // Call activity so the shared prefs can be updated
                callback!!.onFilmSelected(bundle, WATCHLIST_FILM_CONTEXT_ACTION_TYPE.WATCHLIST_REMOVE)
            }
            else -> true
        }

        return super.onContextItemSelected(item)
    }

    override fun onWatchedDialogSubmissionListener(timelineItem: TimelineItem) {
        // Put values in bundle
        val bundle = Bundle()
        bundle.putParcelable("timelineItem", timelineItem)

        // Call listener
        callback!!.onFilmSelected(bundle, WATCHLIST_FILM_CONTEXT_ACTION_TYPE.WATCHLIST_MARK_WATCHED)

        // Removal
        watchlist.remove(timelineItem.film)
        val adapter = this.recyclerView.adapter as WatchlistRecyclerAdapter
        adapter.removeFilmFromWatchlist(timelineItem.film)
    }

    companion object {

        fun newInstance(resultList: ArrayList<FilmThumbnail>): WatchlistFragment {
            val fragment = WatchlistFragment()
            val args = Bundle()
            args.putParcelableArrayList(ARG_LIST, resultList)
            fragment.arguments = args
            return fragment
        }
    }
}

// Dialog fragment called when a film is marked watched in the context menu
class WatchedDialogFragment() : DialogFragment(), RatingBar.OnRatingBarChangeListener, CompoundButton.OnCheckedChangeListener, View.OnClickListener {
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

    private var rating: Int? = null
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
        return inflater.inflate(R.layout.fragment_watchlist_watched_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { // Called after onCreateView, used to set up the widgets
        super.onViewCreated(view, savedInstanceState)

        // todo: synthetic imports?
        poster = view.findViewById(R.id.fragment_watchlist_watched_dialog_poster_iv)
        ratingBar = view.findViewById(R.id.fragment_watchlist_watched_dialog_ratingBar)
        reviewEditText = view.findViewById(R.id.fragment_watchlist_watched_dialog_review_et)
        toggleWatched = view.findViewById(R.id.fragment_watchlist_watched_dialog_toggleWatched)
        cancelButton = view.findViewById(R.id.fragment_watchlist_watched_dialog_cancelButton)
        doneButton = view.findViewById(R.id.fragment_watchlist_watched_dialog_cancelButton)

        ratingBar.setOnRatingBarChangeListener(this)
        toggleWatched.setOnCheckedChangeListener(this)


        Picasso.get().load(film.posterURL).error(R.drawable.placeholder_imageloading)
                .placeholder(R.drawable.placeholder_imageloading).into(poster)

        // ?
        dialog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
        this.rating = rating.toInt()
    }

    override fun onClick(v: View?) {
        if (view == cancelButton) {
            // Close the dialog
            this.dismiss()
        } else if (view == doneButton) {
            // We send all the info to the Watchlist Fragment as a timeline item
            val date = LocalDate.now()
            val text = reviewEditText.text.toString()
            val item = TimelineItem(film, rating, date, text, status)
            callback.onWatchedDialogSubmissionListener(item)
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
