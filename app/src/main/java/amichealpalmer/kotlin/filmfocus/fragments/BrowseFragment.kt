package amichealpalmer.kotlin.filmfocus.fragments

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.activities.MainActivity
import amichealpalmer.kotlin.filmfocus.adapters.BrowseRecyclerAdapter
import amichealpalmer.kotlin.filmfocus.data.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.data.json.GetJSONSearch
import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


//private const val ARG_RESULTS = "resultList"
private const val ARG_SEARCH_STRING = "searchString"

class BrowseFragment : Fragment() {

    internal var callback: onRequestResultsListener? = null
    var resultList = ArrayList<FilmThumbnail>()
    lateinit var recyclerView: RecyclerView
    private val TAG = "BrowseFragment"
    private var noMoreResults = false
    lateinit var searchString: String
    private var currentPage = 1

    interface onRequestResultsListener {
        fun onRequestResults(fragment: BrowseFragment)
    }

    fun setOnRequestResultsListener(callback: onRequestResultsListener) {
        this.callback = callback
    }

    override fun onCreate(savedInstanceState: Bundle?) { // Should save the state if user switches between fragments
        Log.d(TAG, ".onCreate called")
        if (arguments != null) {
            Log.d(TAG, ".onCreateView: arguments != null. setting query var")
            searchString = arguments!!.getString(ARG_SEARCH_STRING)!!
        } else {
            Log.d(TAG, ".onCreateView: arguments is null")
        }
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        //searchHelper().searchByTitleKeyword(searchString)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Get the resultList
        //searchHelper().searchByTitleKeyword(searchString)

        // Inflate the layout for this fragment
        Log.d(TAG, ".onCreateView called")
        var view = inflater.inflate(R.layout.fragment_browse, container, false)
        recyclerView = view.findViewById<RecyclerView>(R.id.browse_films_recyclerview_id)
        recyclerView.layoutManager = GridLayoutManager(activity, 3)
        recyclerView.adapter = BrowseRecyclerAdapter(activity!!, resultList)
        val recyclerAdapter = recyclerView.adapter as BrowseRecyclerAdapter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                // could perhaps rewrite this so it loads new entries before the bottom is reached, right now it is jarring
                if (!recyclerView.canScrollVertically(1)) { // todo: UI and backend logic are completely wrapped up together using this method
                    if (!noMoreResults) {
                        Toast.makeText(activity, "Reached last row - attempting to load more items", Toast.LENGTH_SHORT).show()
                        searchHelper().searchByTitleKeyword(searchString)
//                        val activity = callback as MainActivity
//                        activity.onRequestResults(this@BrowseFragment)


                    }
                }
            }
        })
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Log.d(TAG, ".onCreateOptionsMenu called")
        inflater.inflate(R.menu.browse_fragment_menu, menu)


        val searchView = SearchView((context as MainActivity).supportActionBar?.themedContext ?: context)
        menu.findItem(R.id.browse_fragment_search).apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItem.SHOW_AS_ACTION_IF_ROOM)
            actionView = searchView
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchString = query.toLowerCase()
                searchHelper().searchByTitleKeyword(query.toLowerCase())
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // Does nothing right now
                return true
            }
        })
        searchView.setOnClickListener {view ->  }

        // Associating searchable configuration with the SearchView
        //val componentName = ComponentName(this, MainActivity::class.java)
        //val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        // Configuring the SearchView
//        val searchView = menu.findItem(R.id.search).actionView as SearchView
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
//        searchView.isIconifiedByDefault = false
//        searchView.requestFocus()
        super.onCreateOptionsMenu(menu, inflater)

    }

    inner class searchHelper {
        val activity = callback as MainActivity
        lateinit var query: String
        fun searchByTitleKeyword(titleContains: String) {
            Log.d(TAG, ".searchByTitleKeyword starts")
            query = "?s=$titleContains&page=$currentPage" // Indicates searchHelper by title
            currentPage++
            GetJSONSearch(this, (activity.getString(R.string.OMDB_API_KEY))).execute(query) // Call class handling API searchHelper queries
        }


        fun onSearchResultsDownload(resultList: ArrayList<FilmThumbnail?>) {
            //resultList.addAll(resultList)
            val adapter = recyclerView.adapter as BrowseRecyclerAdapter
            if (resultList.size > 0) {
                adapter.updateList(resultList as List<FilmThumbnail>)
            } else {
                noMoreResults = true
            }

        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BrowseFragment.onRequestResultsListener) {
            callback = context
        } else {
            throw RuntimeException(context.toString() + " must implement onRequestResultsListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    companion object {

        fun newInstance(searchString: String?): BrowseFragment {
            val fragment = BrowseFragment()
            val args = Bundle()
            if (searchString != null) {
                //args.putParcelableArrayList(ARG_RESULTS, resultList)
                args.putString(ARG_SEARCH_STRING, searchString)
                fragment.arguments = args
            }
            return fragment
        }

    }

}
