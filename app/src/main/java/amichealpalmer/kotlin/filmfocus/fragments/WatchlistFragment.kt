package amichealpalmer.kotlin.filmfocus.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.activities.MainActivity
import amichealpalmer.kotlin.filmfocus.adapters.WatchlistRecyclerAdapter
import amichealpalmer.kotlin.filmfocus.data.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.data.TimelineItem
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.joda.time.LocalDate

private const val ARG_LIST = "watchlist"

enum class WATCHLIST_FILM_CONTEXT_ACTION_TYPE { // todo: Should this be somewhere else?
    WATCHLIST_REMOVE, WATCHLIST_MARK_WATCHED
}

class WatchlistFragment : Fragment() { // note: code duplication with browsefragment. possibly have browsefragment and searchfragment/watchlistfragment subclasses

    //private var listener: OnFragmentInteractionListener? = null
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
        //super.onCreateOptionsMenu(menu, inflater)
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
        Log.d(TAG, "menu item: ${item}")
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

                // Construct a TimelineItem
                // todo: prompt user for review and rating properly
                val rating = 3
                val review = "I Liked this film quite a bit!"
                val timelineItem = TimelineItem(film, rating, LocalDate.now(), review)
                val bundle = Bundle()
                bundle.putParcelable("timelineItem", timelineItem)
                // Call listener
                callback!!.onFilmSelected(bundle, WATCHLIST_FILM_CONTEXT_ACTION_TYPE.WATCHLIST_MARK_WATCHED)

                // Removal
                watchlist.remove(film)
                adapter.removeFilmFromWatchlist(film)
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
