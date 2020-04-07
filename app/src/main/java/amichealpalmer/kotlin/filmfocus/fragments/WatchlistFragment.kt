package amichealpalmer.kotlin.filmfocus.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.activities.MainActivity
import amichealpalmer.kotlin.filmfocus.adapters.WatchlistRecyclerAdapter
import amichealpalmer.kotlin.filmfocus.data.Film
import amichealpalmer.kotlin.filmfocus.data.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.data.TimelineItem
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_dialog_generic_confirm.*
import kotlinx.android.synthetic.main.fragment_watchlist.*
import java.lang.NullPointerException

private const val ARG_LIST = "watchlist"

enum class WATCHLIST_FILM_CONTEXT_ACTION_TYPE { // todo: Should this be somewhere else?
    WATCHLIST_REMOVE, WATCHLIST_MARK_WATCHED
}

enum class WATCHLIST_MENU_ITEM_ACTION_TYPE {
    REMOVE_ALL
}

class WatchlistFragment : Fragment(), WatchedDialogFragment.onWatchedDialogSubmissionListener, WatchlistConfirmDeleteDialogFragment.onWatchlistConfirmDeleteDialogListener { // note: code duplication with browsefragment. possibly have browsefragment and searchfragment/watchlistfragment subclasses todo: minimize duplication

    private val TAG = "WatchlistFragment"
    internal var callback: OnWatchlistActionListener? = null
    private lateinit var watchlist: ArrayList<FilmThumbnail>
    lateinit var recyclerView: RecyclerView

    fun setOnFilmSelectedListener(callback: OnWatchlistActionListener) {
        this.callback = callback
    }

    interface OnWatchlistActionListener {
        fun onFilmSelected(bundle: Bundle, typeWATCHLIST: WATCHLIST_FILM_CONTEXT_ACTION_TYPE)
        fun onWatchlistMenuItemSelected(bundle: Bundle, actionType: WATCHLIST_MENU_ITEM_ACTION_TYPE)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, ".onCreate starts")
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

        var view = inflater.inflate(R.layout.fragment_watchlist, container, false)
        recyclerView = view.findViewById<RecyclerView>(R.id.watchlist_recyclerview)
        recyclerView.layoutManager = GridLayoutManager(activity, 3)
        recyclerView.adapter = WatchlistRecyclerAdapter(activity!!, watchlist)
        return view
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        Log.d(TAG, ".onViewStateRestored starts")
        super.onViewStateRestored(savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { // todo: is this recalled when a film is removed (notifyDataSetChanged?) probably not
        onWatchlistStateChange()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnWatchlistActionListener) {
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
        //Log.d(TAG, ".onCreateOptionsMenu called")
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
            override fun onQueryTextSubmit(query: String): Boolean {
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
                fragment.show(fragmentManager!!, "fragment_confirm_clear_watchlist_dialog")
                return true
            }
            //else -> return true
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
                dialogFragment.show(fragmentManager!!, "fragment_watched_dialog")
            }
            R.id.film_thumbnail_context_menu_remove -> {
                val film = adapter.getItem(position)
                watchlist.remove(film)
                adapter.removeFilmFromWatchlist(film)
                onWatchlistStateChange()
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
        onWatchlistStateChange()
        val adapter = this.recyclerView.adapter as WatchlistRecyclerAdapter
        adapter.removeFilmFromWatchlist(timelineItem.film)
    }

    // Clears the watchlist when the user confirms in the dialog prompt
    override fun onWatchlistConfirmDeleteDialogSubmit() {
        val bundle = Bundle()
        Log.d(TAG, ".onWatchlistConfirmDeleteDialogSubmit: watchlist size is ${watchlist.size}")
        bundle.putParcelableArrayList("watchlist", watchlist)
        //val watchlistTest = bundle.getParcelableArrayList<FilmThumbnail>("watchlist")
        //Log.d(TAG, "watchlist test is size ${watchlistTest!!.size}")
        val currentWatchlist = ArrayList<FilmThumbnail>()
        currentWatchlist.addAll(watchlist)
        bundle.putParcelableArrayList("watchlist", currentWatchlist)
        watchlist.clear()
        onWatchlistStateChange()
        val recyclerAdapter = recyclerView.adapter as WatchlistRecyclerAdapter
        recyclerAdapter.clearWatchlist()
        Log.d(TAG, ".onWatchlistConfirmDeleteDialogSubmit: watchlist size is now ${watchlist.size}")
        callback!!.onWatchlistMenuItemSelected(bundle, WATCHLIST_MENU_ITEM_ACTION_TYPE.REMOVE_ALL)

    }

    // Called when any action which might result in an empty watchlist is taken, so we can show the empty view if need be
    // todo: null safety?
    private fun onWatchlistStateChange(){
        if (watchlist.isNotEmpty()) {
            fragment_watchlist_empty_view_container.visibility = View.GONE
            watchlist_recyclerview.visibility = View.VISIBLE
        } else {
            watchlist_recyclerview.visibility = View.GONE
            fragment_watchlist_empty_view_container.visibility = View.VISIBLE
        }
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

// Prompt inflated when the user chooses to clear the watchlist from the app bar menu
class WatchlistConfirmDeleteDialogFragment : DialogFragment(), View.OnClickListener {

    private val TAG = "WatchlistConfirmDelDia"
    private lateinit var callback: onWatchlistConfirmDeleteDialogListener

    interface onWatchlistConfirmDeleteDialogListener {
        fun onWatchlistConfirmDeleteDialogSubmit()
    }

    fun setOnWatchlistConfirmDeleteDialogListener(callback: onWatchlistConfirmDeleteDialogListener) {
        this.callback = callback
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        isCancelable = true
        return inflater.inflate(R.layout.fragment_dialog_generic_confirm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragment_dialog_generic_prompt_text.setText(R.string.dialog_clear_watchlist_prompt)
        fragment_dialog_generic_takeActionButton.setText(R.string.button_clear)

        fragment_dialog_generic_cancelButton.setOnClickListener(this)
        fragment_dialog_generic_takeActionButton.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        Log.d(TAG, ".onClick triggered")
        when (v?.id) {
            fragment_dialog_generic_cancelButton.id -> this.dismiss()
            fragment_dialog_generic_takeActionButton.id -> {
                callback.onWatchlistConfirmDeleteDialogSubmit()
                this.dismiss()
            }
        }
    }

    companion object {
        fun newInstance(callback: onWatchlistConfirmDeleteDialogListener): WatchlistConfirmDeleteDialogFragment {
            val fragment = WatchlistConfirmDeleteDialogFragment()
            fragment.setOnWatchlistConfirmDeleteDialogListener(callback)
            return fragment
        }
    }
}