package amichealpalmer.kotlin.filmfocus.activities

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.adapters.BrowseRecyclerAdapter
import amichealpalmer.kotlin.filmfocus.data.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.data.json.GetJSONSearch
import android.app.Activity
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : Activity() {

    private val TAG = "SearchActivity"

    companion object Mutex {
        var mutex = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (mutex > 0) { // Attempt to prevent double call
            return
        } else {
            handleIntent(intent)
        }
    }

    override fun onNewIntent(intent: Intent) {
        Log.d(TAG, ".onNewIntent called")
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (mutex == 0) {
            mutex++
            Log.d(TAG, ".handleIntent started")
            if (Intent.ACTION_SEARCH == intent.action) {
                val query = intent.getStringExtra(SearchManager.QUERY)
                Log.d(TAG, ".handleIntent: received new search query: $query")
                // todo: search with the query
                searchByTitleKeyword(query)
            } else {
                Log.d(TAG, "intent.action != Intent.ACTION_SEARCH")
            }
        }
    }

    private fun searchByTitleKeyword(titleContains: String) {
        Log.d(TAG, ".searchByTitleKeyword starts")
        val query = "?s=$titleContains" // Indicates search by title
        GetJSONSearch(this, (this.getString(R.string.OMDB_API_KEY))).execute(query) // Call class handling API search queries
    }

//    fun onResultListDownloadComplete(resultList: ArrayList<FilmThumbnail?>) {
//        // this.resultList = resultList
//        Log.d(TAG, ".onJSONDownloadComplete: retrieved and set list of search results of size ${resultList.size}")
//        displaySearchResults(resultLI)
//    }

    fun displaySearchResults(resultList: ArrayList<FilmThumbnail?>) { // this should be moved out into the subclasses
        // Testing results view
        Log.d(TAG, ".displayThumbnails called. Attempting to display search result list")
        val results = resultList as List<FilmThumbnail>

        //val resultList = intent.getParcelableArrayListExtra<FilmThumbnail>("thumbs")
        //val displayContext = intent.getSerializableExtra("displaycontext") as DisplayContext
        //Log.d(TAG, ".displayThumbnails: got data from intent")

        setContentView(R.layout.browse_films)
        //val myAdapter = BrowseRecyclerAdapter(this, results)
        val recyclerView = findViewById<RecyclerView>(R.id.browse_films_recyclerview_id)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.adapter = BrowseRecyclerAdapter(this, results)

        if (mutex > 0) {
            mutex = 0
        }
        Log.d(TAG, ".displaySearchResults complete")
    }

}