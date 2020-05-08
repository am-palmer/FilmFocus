package amichealpalmer.kotlin.filmfocus.view

import amichealpalmer.kotlin.filmfocus.MainActivity
import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.adapters.WatchlistRecyclerAdapter
import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.model.TIMELINE_ITEM_STATUS
import amichealpalmer.kotlin.filmfocus.model.TimelineItem
import amichealpalmer.kotlin.filmfocus.view.dialog.WatchedDialogFragment
import amichealpalmer.kotlin.filmfocus.view.dialog.WatchlistConfirmDeleteDialogFragment
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_watchlist.*
import java.lang.ref.WeakReference

class WatchlistFragment : Fragment(), WatchedDialogFragment.onWatchedDialogSubmissionListener, WatchlistConfirmDeleteDialogFragment.onWatchlistConfirmDeleteDialogListener { // note: code duplication with browsefragment. possibly have browsefragment and searchfragment/watchlistfragment subclasses todo: minimize duplication

    private val TAG = "WatchlistFragment"
    internal var callback: WatchlistFragmentDataListener? = null
    private lateinit var watchlist: ArrayList<FilmThumbnail>
    lateinit var recyclerView: RecyclerView

    fun setWatchlistFragmentDataListener(callback: WatchlistFragmentDataListener) {
        this.callback = callback
    }

    interface WatchlistFragmentDataListener {
        fun retrieveWatchlist(): ArrayList<FilmThumbnail>
        fun clearWatchlist()
        fun removeFilmFromWatchlist(film: FilmThumbnail)
        fun addItemToTimeline(timelineItem: TimelineItem) // Called when a film is marked watched
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, ".onCreate starts")

        watchlist = if (savedInstanceState == null) {
            // Get the watchlist from SharedPrefs
            callback!!.retrieveWatchlist()
        } else {
            savedInstanceState.getParcelableArrayList("watchlist")
                    ?: throw NullPointerException()
        }
        setHasOptionsMenu(true) // Indicates we want onCreateOptionsMenu to be called
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        Log.d(TAG, ".onCreateView called")

        val view = inflater.inflate(R.layout.fragment_watchlist, container, false)
        recyclerView = view.findViewById(R.id.watchlist_recyclerview)
        recyclerView.adapter = WatchlistRecyclerAdapter(requireActivity(), watchlist, WeakReference(this))
        recyclerView.setHasFixedSize(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        onWatchlistStateChange()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is WatchlistFragmentDataListener) {
            callback = context
        } else {
            throw RuntimeException(context.toString() + " must implement onFilmSelectedListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList("watchlist", watchlist)
        super.onSaveInstanceState(outState)
    }

    // Checking if any of the dialogs associated with this fragment exist and reattaching the listeners
    override fun onResume() {
        Log.d(TAG, ".onResume starts")
        super.onResume()
        val watchedDialogFragment = childFragmentManager.findFragmentByTag(WatchedDialogFragment.TAG)
        if (watchedDialogFragment is WatchedDialogFragment) { // May be null
            watchedDialogFragment.setOnWatchedDialogSubmissionListener(this)
        }
        val watchlistConfirmDeleteDialogFragment = childFragmentManager.findFragmentByTag(WatchlistConfirmDeleteDialogFragment.TAG)
        if (watchlistConfirmDeleteDialogFragment is WatchlistConfirmDeleteDialogFragment) {
            watchlistConfirmDeleteDialogFragment.setOnWatchlistConfirmDeleteDialogListener(this)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.watchlist_fragment_menu, menu)

        val searchView = SearchView((context as MainActivity).supportActionBar?.themedContext
                ?: context)
        searchView.isIconifiedByDefault = false
        searchView.requestFocus()
        menu.findItem(R.id.watchlist_fragment_search).apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItem.SHOW_AS_ACTION_IF_ROOM)
            actionView = searchView
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            val activity = callback as MainActivity

            override fun onQueryTextSubmit(query: String): Boolean {
                activity.closeKeyboard() // Todo: obscene solution
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
        searchView.setOnClickListener { view -> } // ??

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, ".onOptionsItemSelected triggers")
        when (item.itemId) {
            R.id.watchlist_fragment_more_menu_removeAll -> {
                val fragment = WatchlistConfirmDeleteDialogFragment.newInstance(this)
                fragment.show(childFragmentManager, WatchlistConfirmDeleteDialogFragment.TAG)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, ".onContextItemSelected called")
        val adapter = recyclerView.adapter as WatchlistRecyclerAdapter
        var position = -1
        try {
            position = adapter.position
        } catch (e: NullPointerException) {
            Log.d(TAG, e.localizedMessage, e)
            return super.onContextItemSelected(item)
        }
        when (item.itemId) {
            R.id.film_thumbnail_context_menu_mark_watched -> {
                val film = adapter.getItem(position)
                val dialogFragment = WatchedDialogFragment.newInstance(film)
                dialogFragment.setOnWatchedDialogSubmissionListener(this)
                dialogFragment.show(childFragmentManager, WatchedDialogFragment.TAG)
            }
            R.id.film_thumbnail_context_menu_remove -> {
                val film = adapter.getItem(position)
                Toast.makeText(requireContext(), "Removed ${film.title} from Watchlist", Toast.LENGTH_SHORT).show()
                removeFilmFromWatchlist(film)
            }
            else -> true
        }
        return super.onContextItemSelected(item)
    }

    // Called when user submits Watched dialog; calls addFilmToHistory which removes film from the watchlist and adds it to the history
    override fun onWatchedDialogSubmissionListener(timelineItem: TimelineItem) {
        var status = when (timelineItem.status) {
            TIMELINE_ITEM_STATUS.DROPPED -> "Dropped"
            TIMELINE_ITEM_STATUS.WATCHED -> "Watched"
        }
        Toast.makeText(requireContext(), "Marked ${timelineItem.film.title} as $status", Toast.LENGTH_SHORT).show()
        addFilmToHistory(timelineItem)
    }

    override fun onWatchlistConfirmDeleteDialogSubmit() {
        when (watchlist.isEmpty()) {
            true -> Toast.makeText(requireContext(), "Watchlist is already empty", Toast.LENGTH_SHORT).show()
            false -> {
                clearWatchlist()
                Toast.makeText(requireContext(), "Cleared watchlist", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Removes film from the watchlist in the fragment, the adapter, and notifies the activity to save Watchlist to sharedPrefs
    private fun removeFilmFromWatchlist(film: FilmThumbnail) {
        val adapter = recyclerView.adapter as WatchlistRecyclerAdapter
        watchlist.remove(film)
        adapter.removeFilmFromWatchlist(film)
        onWatchlistStateChange()
        // Update SharedPrefs
        callback!!.removeFilmFromWatchlist(film)
    }

    // Completely clears Watchlist
    private fun clearWatchlist() {
        val adapter = recyclerView.adapter as WatchlistRecyclerAdapter
        adapter.clearWatchlist()
        watchlist.clear()
        onWatchlistStateChange()
        // Update SharedPrefs
        callback!!.clearWatchlist()
    }

    private fun addFilmToHistory(timelineItem: TimelineItem) {
        removeFilmFromWatchlist(timelineItem.film)
        // Update SharedPrefs
        callback!!.addItemToTimeline(timelineItem)
    }

    // Called when any action which might result in an empty watchlist is taken, so we can show the empty view if need be
    private fun onWatchlistStateChange() {
        // todo: could have animation because right now change is abrupt
        if (watchlist.isNotEmpty()) {
            fragment_watchlist_empty_view_container.visibility = View.GONE
            watchlist_recyclerview.visibility = View.VISIBLE
        } else {
            watchlist_recyclerview.visibility = View.GONE
            fragment_watchlist_empty_view_container.visibility = View.VISIBLE
        }
    }

}

