package amichealpalmer.kotlin.filmfocus

import android.content.Context
import android.graphics.Movie
import android.util.Log
import org.json.JSONException
import org.json.JSONObject

class Search(val apikey: String, val listener: MainActivity) { // only supports search by title right now TODO: implement search by other values i.e. search by actor or director (if omdb supports it)
    // OMDB returns a JSON set of results containing the search string (by title)

    val TAG = "Search"
    var resultList = ArrayList<GetJSONSearch.Result?>() // Todo better way to do this

    fun searchByTitleKeyword(titleContains: String) {
        Log.d(TAG, ".searchByTitleKeyword starts")
        val query = "?s=$titleContains" // Indicates search by title
        GetJSONSearch(this, apikey).execute(query) // Call class handling API search queries
    }

    fun onResultListDownloadComplete(resultList: ArrayList<GetJSONSearch.Result?>) {
        this.resultList = resultList // We have list of results for search term
        Log.d(TAG, ".onJSONDownloadComplete: retrieved and set list of search results of size ${resultList.size}")
    }


    fun getFilmByID(imdbID: String){
        GetJSONFilm(this, apikey).execute(imdbID)
    }

    fun onFilmInfoDownloadComplete(film: Film){ // Called from our GetJSONFilm class once .doInBackground finishes executing
        /// todo implement better functionality, prevent mud balling
        Log.d(TAG, ".onFilmInfoDownloadComplete called.")
        Log.d(TAG, "FILM DATA: ${film}")
        Log.d(TAG, "passing object to listener")
       // FilmDetailsActivity(film)
        listener.inflateFilmInformation(film)
    }


}


