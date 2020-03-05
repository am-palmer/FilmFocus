package amichealpalmer.kotlin.filmfocus.fragments

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.activities.MainActivity
import amichealpalmer.kotlin.filmfocus.adapters.BrowseRecyclerAdapter
import amichealpalmer.kotlin.filmfocus.data.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.data.json.GetJSONSearch
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


private const val ARG_RESULTS = "resultList"
private const val ARG_SEARCH_STRING = "searchString"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [BrowseFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [BrowseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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

//        fun inflateSearchResultsFragment(resultList: ArrayList<FilmThumbnail?>) {
//            Log.d(TAG, ".onSearchResultsDownload: JSON searchHelper calls listener")
//            Log.d(TAG, ".onSearchResultsDownload: building fragment and replacing main_frame_layout_fragment_holder FrameLayout")
//
//            // Build fragment, pass in data.
//            val fragment = BrowseFragment()
//            var args = Bundle()
//            args.putParcelableArrayList("resultList", resultList)
//            args.putString("searchString", query)
//            fragment.arguments = args
//            var transaction = activity.supportFragmentManager.beginTransaction()
//            transaction.replace(R.id.main_frame_layout_fragment_holder, fragment)
//            transaction.commit()
//        }

    }


//
//    fun onFetchResults(resultList: ArrayList<FilmThumbnail>){
//        val adapter = recyclerView.adapter as BrowseRecyclerAdapter
//        adapter.updateList(resultList)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, ".onCreate called")
        if (arguments != null) {
            Log.d(TAG, ".onCreateView: arguments != null. setting resultList var")
            //resultList = arguments!!.getParcelableArrayList<FilmThumbnail>(ARG_RESULTS) as ArrayList<FilmThumbnail>
            searchString = arguments!!.getString(ARG_SEARCH_STRING)!!
            //       Log.d(TAG, "resultList is: ${resultList}")
        } else {
            Log.d(TAG, ".onCreateView: arguments is null")
        }
        super.onCreate(savedInstanceState)
        searchHelper().searchByTitleKeyword(searchString)
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

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return super.onContextItemSelected(item)
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

        fun newInstance(searchString: String): BrowseFragment {
            val fragment = BrowseFragment()
            val args = Bundle()
            //args.putParcelableArrayList(ARG_RESULTS, resultList)
            args.putString(ARG_SEARCH_STRING, searchString)
            fragment.arguments = args
            return fragment
        }

    }

}
