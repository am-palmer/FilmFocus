package amichealpalmer.kotlin.filmfocus.activities

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.adapters.BrowseRecyclerAdapter
import amichealpalmer.kotlin.filmfocus.data.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.data.json.GetJSONSearch
import amichealpalmer.kotlin.filmfocus.fragments.BrowseFragment
import android.annotation.SuppressLint
import android.app.Activity
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : BrowseActivity() { // Handles searches made with the search bar

    private val TAG = "SearchActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, ".OnCreate starts"
        )
        super.onCreate(savedInstanceState)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        Log.d(TAG, ".handleIntent started")
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            Log.d(TAG, ".handleIntent: received new search query: $query")
            searchByTitleKeyword(query!!)
        } else {
            Log.d(TAG, "intent.action != Intent.ACTION_SEARCH")
        }
    }

    private fun searchByTitleKeyword(titleContains: String) {
        Log.d(TAG, ".searchByTitleKeyword starts")
        val query = "?s=$titleContains" // Indicates search by title
        GetJSONSearch(this, (this.getString(R.string.OMDB_API_KEY))).execute(query) // Call class handling API search queries
    }

    fun displaySearchResults(resultList: ArrayList<FilmThumbnail?>) {
        super.displayBrowseFragment(resultList)
    }

}