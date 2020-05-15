package amichealpalmer.kotlin.filmfocus.view

import amichealpalmer.kotlin.filmfocus.MainActivity
import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.adapter.BrowseRecyclerAdapter
import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.model.entity.TIMELINE_ITEM_STATUS
import amichealpalmer.kotlin.filmfocus.model.entity.TimelineItem
import amichealpalmer.kotlin.filmfocus.model.remote.json.GetJSONSearch
import amichealpalmer.kotlin.filmfocus.view.dialog.WatchedDialogFragment
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_browse.*
import java.lang.ref.WeakReference
import java.util.*


private const val ARG_RESULTS = "resultList"
private const val ARG_SEARCH_STRING = "searchString"


// todo: no longer retains content upon switching fragment in menu

class BrowseFragment : Fragment(), WatchedDialogFragment.onWatchedDialogSubmissionListener {

    internal var callback: onResultActionListener? = null
    private var resultList: ArrayList<FilmThumbnail>? = null
    private lateinit var recyclerView: RecyclerView

    private val TAG = "BrowseFragment"
    private var noMoreResults = false
    var searchString: String? = null
    private var currentPage = 1
    //private var recyclerScollPosition = 0

    interface onResultActionListener {
        fun addFilmToWatchlistFromBrowse(filmThumbnail: FilmThumbnail): Boolean //todo: would be nice if this method could be merged with method in history callback interface
        fun markFilmAsWatchedFromBrowse(timelineItem: TimelineItem) // todo: would be nice if this used the same method as in the watchlist
    }

    fun setOnResultActionListener(callback: onResultActionListener) {
        this.callback = callback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, ".onCreate called")
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        //Restore instance state todo use viewmodel
//        if (savedInstanceState != null) {
//            Log.d(TAG, "savedInstanceState: retrieving search query")
//           // searchString = savedInstanceState.getString(ARG_SEARCH_STRING)
//           // noMoreResults = savedInstanceState.getBoolean("noMoreResults")
//            //currentPage = savedInstanceState.getInt("currentPage")
//        } else {
//            Log.d(TAG, ".onCreateView: saved instance state null")
//        }
        resultList = savedInstanceState?.getParcelableArrayList(ARG_RESULTS)
                ?: ArrayList<FilmThumbnail>()
        // Inflate the layout for this fragment
        Log.d(TAG, ".onCreateView called")
        val view = inflater.inflate(R.layout.fragment_browse, container, false)
        recyclerView = view.findViewById(R.id.browse_films_recyclerview_id)

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        Log.d(TAG, "is resultList null? ${resultList?.size}")

        try {
            recyclerView.adapter = BrowseRecyclerAdapter(requireActivity(), resultList!!, WeakReference(this)) // We pass in the nav controller so we can assign onClick navigation for each search result
            recyclerView.setHasFixedSize(true)
            //Log.d(TAG, ".onCreateView: adapter instantiated")
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    // Could perhaps rewrite this so it loads new entries before the bottom is reached, right now it is jarring. Good functionality: load the first TWO pages at once, then load subsequent page based on scroll position rather than canScrollVertically
                    if (!recyclerView.canScrollVertically(1)) { // todo: UI and backend logic are completely wrapped up together using this method
                        if (!noMoreResults && searchString != null) {
                            SearchHelper().searchByTitleKeyword(searchString!!)
                        }
                    }
                }
            })
        } catch (e: NullPointerException) {
            Log.e(TAG, "onCreateView: npe")
            Log.e(TAG, e.printStackTrace().toString())
        }
        if (savedInstanceState != null) {
            Log.d(TAG, "savedInstanceState not null, scrolling to position")
            recyclerView?.post {
                val pos = savedInstanceState.getInt("recyclerScrollPosition")
                recyclerView?.scrollToPosition(pos)
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Set the empty view as visible by default, turn it off once a query is entered
        if (resultList!!.size > 0) {
            fragment_search_empty_container.visibility = View.GONE
            fragment_browse_recycler_framelayout.visibility = View.VISIBLE
        } else {
            if (searchString.isNullOrBlank()) {
                fragment_search_empty_container.visibility = View.VISIBLE
                fragment_browse_recycler_framelayout.visibility = View.GONE
            } else {
                fragment_search_empty_container.visibility = View.GONE
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) { // Called when i.e. screen orientation changes
        super.onSaveInstanceState(outState)
        if (searchString != null) {
            outState.putString(ARG_SEARCH_STRING, searchString)
        }
        if (!resultList.isNullOrEmpty()) {
            outState.putParcelableArrayList(ARG_RESULTS, resultList)
        }

        var scrollPos: Int? = null
        if (recyclerView?.adapter != null) {
            val adapter = recyclerView?.adapter as BrowseRecyclerAdapter
            scrollPos = adapter.getAdapterPosition()
        }
        outState.putInt("recyclerScrollPosition", scrollPos ?: 0)
//        outState.putInt("currentPage", currentPage)
//        outState.putBoolean("noMoreResults", noMoreResults)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is onResultActionListener) {
            callback = context
        } else {
            throw RuntimeException(context.toString() + " must implement onRequestResultsListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    // Reattaching listener interface to dialogs if they exist
    override fun onResume() {
        super.onResume()
        val watchedDialogFragment = childFragmentManager.findFragmentByTag(WatchedDialogFragment.TAG)
        if (watchedDialogFragment is WatchedDialogFragment) {
            watchedDialogFragment.setOnWatchedDialogSubmissionListener(this)
        }
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
                // We reset the fields holding search data for a new search
//                currentPage = 1
//                noMoreResults = false
                searchString = query.toLowerCase(Locale.US).trim()

                // Clear the UI
        //        resultList?.clear()
                fragment_search_empty_container.visibility = View.GONE
                fragment_browse_recycler_framelayout.visibility = View.VISIBLE
                val adapter = recyclerView?.adapter
                if (adapter is BrowseRecyclerAdapter) {
                    adapter.clearList()
                }


                SearchHelper().searchByTitleKeyword(searchString!!)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean { // Unused in this context
                return true
            }
        })
        searchView.setOnClickListener { view -> }

        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onContextItemSelected(item: MenuItem): Boolean { // todo: code duplication with watchlistRecyclerAdapter
        try {
            Log.d(TAG, ".onContextItemSelected called")
            Log.d(TAG, "menu item: ${item}")
            val adapter = recyclerView?.adapter as BrowseRecyclerAdapter
            var position: Int
            try {
                position = adapter.position
            } catch (e: NullPointerException) {
                Log.d(TAG, e.localizedMessage, e)
                return super.onContextItemSelected(item)
            }

            when (item.itemId) {
                R.id.browse_film_context_menu_add -> {
                    val film = adapter.getItem(position)
                    when (addFilmToWatchlist(film)) { // Note secondary effect
                        true -> Toast.makeText(requireContext(), "Added ${film.title} to Watchlist", Toast.LENGTH_SHORT).show()
                        false -> Toast.makeText(requireContext(), "${film.title} is already in Watchlist", Toast.LENGTH_SHORT).show()
                    }
                }
                R.id.browse_film_context_menu_mark_watched -> {
                    val film = adapter.getItem(position)
                    val dialogFragment = WatchedDialogFragment.newInstance(film)
                    dialogFragment.setOnWatchedDialogSubmissionListener(this)
                    dialogFragment.show(childFragmentManager, WatchedDialogFragment.TAG)
                }
                else -> true
            }

            return super.onContextItemSelected(item)

        } catch (e: NullPointerException) {
            Log.e(TAG, ".onContextItemSelected: NPE - callback null?")
        }
        return false
    }

//    inner class SearchHelper {
//        //private val activity = callback as MainActivity
//
//        fun searchByTitleKeyword(titleContains: String) { // This method is called multiple times to load each subsequent page
//            Log.d(TAG, ".searchByTitleKeyword starts")
//            val query = "?s=$titleContains&page=$currentPage" // Indicates searchHelper by title
//            currentPage++
//            browse_fragment_progressBar?.visibility = View.VISIBLE // todo: need this call somewhere
//            GetJSONSearch(WeakReference(this), (activity.getString(R.string.OMDB_API_KEY))).execute(query) // Call class handling API searchHelper queries
//        }
//
//        fun onSearchResultsDownload(resultList: ArrayList<FilmThumbnail?>) {
//            browse_fragment_progressBar?.visibility = View.GONE
//            val adapter = recyclerView?.adapter as BrowseRecyclerAdapter
//            Log.d(TAG, "onSearchResultsDownload: RESULTLIST IS EMPTY? ${resultList.isEmpty()}")
//            Log.d(TAG, "and CurrentPage is: $currentPage")
//            if (resultList.isEmpty() && currentPage == 2) { // Indicates there are no results for the search term. Todo: magic numbers... also doesn't work as intended
//                Log.d(TAG, "onSearchResultsDownload -> no results, showing no results view")
//                fragment_browse_no_results_container?.visibility = View.VISIBLE
//                fragment_browse_recycler_framelayout?.visibility = View.GONE
//                fragment_search_empty_container?.visibility = View.GONE
//            } else {
//                Log.d(TAG, ".onSearchResultsDownload -> results found, showing results in recyclerview")
//                fragment_browse_no_results_container?.visibility = View.GONE
//                fragment_browse_recycler_framelayout?.visibility = View.VISIBLE
//                if (resultList.size > 0) {
//                    adapter.updateList(resultList as List<FilmThumbnail>)
//                } else {
//                    noMoreResults = true
//                }
//            }
//
//        }
//
//    }

    override fun onWatchedDialogSubmissionListener(timelineItem: TimelineItem) {
        var status = when (timelineItem.status) {
            TIMELINE_ITEM_STATUS.DROPPED -> "Dropped"
            TIMELINE_ITEM_STATUS.WATCHED -> "Watched"
        }
        Toast.makeText(requireContext(), "Marked ${timelineItem.film.title} as $status", Toast.LENGTH_SHORT).show()
        markFilmAsWatched(timelineItem)
    }

    private fun addFilmToWatchlist(film: FilmThumbnail): Boolean {
        return callback!!.addFilmToWatchlistFromBrowse(film)
    }

    private fun markFilmAsWatched(timelineItem: TimelineItem) {
        callback!!.markFilmAsWatchedFromBrowse(timelineItem)
    }

    companion object {

        fun newInstance(searchString: String?): BrowseFragment {
            val fragment = BrowseFragment()
            val args = Bundle()
            if (searchString != null) {
                args.putString(ARG_SEARCH_STRING, searchString)
            }
            fragment.arguments = args
            return fragment
        }

    }

}
