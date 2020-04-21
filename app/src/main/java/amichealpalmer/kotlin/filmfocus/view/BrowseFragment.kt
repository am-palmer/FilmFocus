package amichealpalmer.kotlin.filmfocus.view

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.adapters.BrowseRecyclerAdapter
import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.model.TimelineItem
import amichealpalmer.kotlin.filmfocus.utilities.GetJSONSearch
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_browse.*
import java.util.*
import kotlin.collections.ArrayList


private const val ARG_RESULTS = "resultList"
private const val ARG_SEARCH_STRING = "searchString"

enum class BROWSE_FILM_CONTEXT_ACTION_TYPE {
    ADD_TO_WATCHLIST, MARK_WATCHED
}

class BrowseFragment : Fragment(), WatchedDialogFragment.onWatchedDialogSubmissionListener {

    internal var callback: onResultActionListener? = null
    private var resultList: ArrayList<FilmThumbnail>? = null
    private var recyclerView: RecyclerView? = null

    //var progressBar: ProgressBar? = null
    private val TAG = "BrowseFragment"
    private var noMoreResults = false
    var searchString: String? = null
    private var currentPage = 1
    // private var recyclerScollPosition = 0

    interface onResultActionListener {
        fun onSearchResultAction(bundle: Bundle, type: BROWSE_FILM_CONTEXT_ACTION_TYPE)
    }

    fun setOnResultActionListener(callback: onResultActionListener) {
        this.callback = callback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, ".onCreate called")
        if (savedInstanceState != null) {

            // Todo: we should also restore the position in the scroll view
            Log.d(TAG, "savedInstanceState: retrieving search query")
            searchString = savedInstanceState.getString(ARG_SEARCH_STRING)
            resultList = savedInstanceState.getParcelableArrayList<FilmThumbnail>(ARG_RESULTS)
                    ?: ArrayList<FilmThumbnail>()
            noMoreResults = savedInstanceState.getBoolean("noMoreResults")
            // recyclerScollPosition = savedInstanceState.getInt("recyclerScrollPosition")
        } else {
            resultList = ArrayList<FilmThumbnail>()
        }
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        Log.d(TAG, ".onCreateView called")
        val view = inflater.inflate(R.layout.fragment_browse, container, false)
        recyclerView = view.findViewById<RecyclerView>(R.id.browse_films_recyclerview_id)

        // Check current orientation so we can change number of items displayed per row in the adapter
        when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> recyclerView?.layoutManager = GridLayoutManager(activity, 3)
            Configuration.ORIENTATION_LANDSCAPE -> recyclerView?.layoutManager = GridLayoutManager(activity, 5)
        }

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        try {
            Log.d(TAG, "onCreateView: trying")
            recyclerView?.adapter = BrowseRecyclerAdapter(activity!!, resultList!!)
            Log.d(TAG, "onCreateView: adapter is initiated")
            recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    // could perhaps rewrite this so it loads new entries before the bottom is reached, right now it is jarring
                    if (!recyclerView.canScrollVertically(1)) { // todo: UI and backend logic are completely wrapped up together using this method
                        if (!noMoreResults && searchString != null) {
                            searchHelper().searchByTitleKeyword(searchString!!)
                        }
                    }
                }
            })
        } catch (e: NullPointerException) {
            Log.e(TAG, "onCreateView: npe")
            Log.e(TAG, e.printStackTrace().toString())
        }
        if (savedInstanceState != null) {
            recyclerView?.post({
                val pos = savedInstanceState.getInt("recyclerScrollPosition")
                recyclerView?.scrollToPosition(pos)
            })
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Set the empty view as visible by default, turn it off once a query is entered
        if (savedInstanceState != null && resultList!!.size > 0) {
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
        if (resultList != null) {
            outState.putParcelableArrayList(ARG_RESULTS, resultList)
        }

        var scrollPos: Int? = null
        if (recyclerView?.adapter != null) {
            val adapter = recyclerView?.adapter as BrowseRecyclerAdapter
            scrollPos = adapter.getAdapterPosition()
        }
        outState.putInt("recyclerScrollPosition", scrollPos ?: 0)
        outState.putBoolean("noMoreResults", noMoreResults)
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
                currentPage = 1
                noMoreResults = false
                searchString = query.toLowerCase(Locale.US).trim()

                // Clear the UI
                resultList?.clear()
                fragment_search_empty_container.visibility = View.GONE
                fragment_browse_recycler_framelayout.visibility = View.VISIBLE
                val adapter = recyclerView?.adapter as BrowseRecyclerAdapter
                adapter.clearList()

                searchHelper().searchByTitleKeyword(searchString!!)
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
            var position = -1
            try {
                position = adapter.position
            } catch (e: java.lang.Exception) { // todo: too generalized, catch specific exceptions
                Log.d(TAG, e.localizedMessage, e)
                return super.onContextItemSelected(item)
            }
            when (item.itemId) {
                R.id.browse_film_context_menu_add -> {
                    val film = adapter.getItem(position)
                    val bundle = Bundle()
                    bundle.putParcelable("film", film)
                    callback!!.onSearchResultAction(bundle, BROWSE_FILM_CONTEXT_ACTION_TYPE.ADD_TO_WATCHLIST)
                }
                R.id.browse_film_context_menu_mark_watched -> {
                    // todo: code duplication with watchlist fragment
                    val film = adapter.getItem(position)
                    // todo: prompt user for review and rating properly
                    val dialogFragment = WatchedDialogFragment.newInstance(film)
                    dialogFragment.setOnWatchedDialogSubmissionListener(this)
                    dialogFragment.show(fragmentManager!!, "fragment_watched_dialog")
                }
                else -> true
            }
            return super.onContextItemSelected(item)

        } catch (e: NullPointerException) {
            Log.e(TAG, ".onContextItemSelected: NPE - callback null?")
        }
        return false
    }

    inner class searchHelper {
        private val activity = callback as MainActivity

        fun searchByTitleKeyword(titleContains: String) { // This method is called multiple times to load each subsequent page
            Log.d(TAG, ".searchByTitleKeyword starts")
            val query = "?s=$titleContains&page=$currentPage" // Indicates searchHelper by title
            currentPage++
            browse_fragment_progressBar?.visibility = View.VISIBLE
            GetJSONSearch(this, (activity.getString(R.string.OMDB_API_KEY))).execute(query) // Call class handling API searchHelper queries
        }


        fun onSearchResultsDownload(resultList: ArrayList<FilmThumbnail?>) {
            browse_fragment_progressBar?.visibility = View.GONE
            val adapter = recyclerView?.adapter as BrowseRecyclerAdapter
            Log.d(TAG, "onSearchResultsDownload: RESULTLIST IS EMPTY? ${resultList.isEmpty()}")
            Log.d(TAG, "and CurrentPage is: $currentPage")
            if (resultList.isEmpty() && currentPage == 2) { // Indicates there are no results for the search term. Todo: magic numbers... also doesn't work as intended
                Log.d(TAG, "onSearchResultsDownload -> no results, showing no results view")
                fragment_browse_no_results_container?.visibility = View.VISIBLE
                fragment_browse_recycler_framelayout?.visibility = View.GONE
                fragment_search_empty_container?.visibility = View.GONE
            } else {
                Log.d(TAG, ".onSearchResultsDownload -> results found, showing results in recyclerview")
                fragment_browse_no_results_container?.visibility = View.GONE
                fragment_browse_recycler_framelayout?.visibility = View.VISIBLE
                if (resultList.size > 0) {
                    adapter.updateList(resultList as List<FilmThumbnail>)
                } else {
                    noMoreResults = true
                }
            }

        }

    }

    override fun onWatchedDialogSubmissionListener(timelineItem: TimelineItem) { // todo: code duplication with watchlist fragment
        // Put values in bundle
        val bundle = Bundle()
        bundle.putParcelable("timelineItem", timelineItem)

        // Call listener
        callback!!.onSearchResultAction(bundle, BROWSE_FILM_CONTEXT_ACTION_TYPE.MARK_WATCHED)
    }

    companion object {

        fun newInstance(searchString: String?): BrowseFragment {
            val fragment = BrowseFragment()
            val args = Bundle()
            if (searchString != null) {
                //args.putParcelableArrayList(ARG_RESULTS, resultList)
                args.putString(ARG_SEARCH_STRING, searchString)
            }
//            if (resultList != null) {
//                args.putParcelableArrayList("resultList", resultList)
//            }
            fragment.arguments = args
            return fragment
        }

    }

}
