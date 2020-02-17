package amichealpalmer.kotlin.filmfocus

import android.content.Context
import android.graphics.Movie
import android.util.Log
import org.json.JSONException
import org.json.JSONObject

// Handles searches which return ArrayLists of Result objects

class Search(val listener: MainActivity) { // only supports search by title right now TODO: implement search by other values i.e. search by actor or director (if omdb supports it)
    // OMDB returns a JSON set of results containing the search string (by title)

    val TAG = "Search"
    var resultList = ArrayList<GetJSONSearch.Result?>()

    fun searchByTitleKeyword(titleContains: String) {
        Log.d(TAG, ".searchByTitleKeyword starts")
        val query = "?s=$titleContains" // Indicates search by title
        GetJSONSearch(this, listener.getString(R.string.OMDB_API_KEY)).execute(query) // Call class handling API search queries
    }

    fun onResultListDownloadComplete(resultList: ArrayList<GetJSONSearch.Result?>) {
        this.resultList = resultList // We have list of results for search term
        Log.d(TAG, ".onJSONDownloadComplete: retrieved and set list of search results of size ${resultList.size}")
        listener.displaySearchResults(resultList as List<GetJSONSearch.Result>)
    }


}


