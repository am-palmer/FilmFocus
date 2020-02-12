package amichealpalmer.kotlin.filmfocus

import android.content.Context
import android.graphics.Movie
import android.util.Log
import org.json.JSONException
import org.json.JSONObject

class Search(val apikey: String) { // only supports search by title right now TODO: implement search by other values i.e. search by actor or director (if omdb supports it)
    // OMDB returns a JSON set of results containing the search string (by title)

    val TAG = "Search"
    val OMDBurl = "https://www.omdbapi.com/"
    private var resultList = ArrayList<Result>()

    //https://www.omdbapi.com/?s=ghost&apikey=d0231b9b
    // simplify this later. there's probably a better way to do it


    // make a call to the api with a generic class which returns us the JSON data

    fun searchByTitleKeyword(titleContains: String) {
        val query = "?s=$titleContains" // Indicates search by title
        // val OMDBurl = "https://www.omdbapi.com/"
        val getOMDBJsonData =
            GetOMDBJsonData(this, apikey, ResultType.SEARCH_RESULT_LIST).execute(OMDBurl + query)
    }

    fun onJSONDownloadComplete(result: JSONObject?, resultType: ResultType) {
        if (result == null) {
            Log.e(
                TAG,
                ".onJSONDownloadComplete: Null JSONObject - failure to catch in GetOMDBJsonData?"
            )
            // Display a toast message maybe?
        } else {
            // use when?
            if (resultType == ResultType.SEARCH_RESULT_LIST)
            // parse json object and make result objects
            // May want to split this into a helper class?
            // Call our helper method to make results
                createResultsFromJSON(result!!)
        }
        if (resultType == ResultType.FILM_INFORMATION) {
            // construct a film object
            createFilmFromJSON(result!!)
        }


    }

    // todo: seperate out json parsing logic from search class

    private fun createFilmFromJSON(jsonItem: JSONObject) { // should be returned somewhere
        Log.d(TAG, ".createFilmFromJSON starting")

        try {
            val title = jsonItem.getString("Title")
            val year = jsonItem.getString("Year")
            val rated = jsonItem.getString("Rated")
            val released = jsonItem.getString("Released")
            val runtime = jsonItem.getString("Runtime")
            val genre = jsonItem.getString("Genre")
            val director = jsonItem.getString("Director")
            //val writer = jsonItem.getString("Writer")
            val actors = jsonItem.getString("Actors")
            val plot = jsonItem.getString("Plot")
            val language = jsonItem.getString("Language")
            val country = jsonItem.getString("Country")
            val awards = jsonItem.getString("Awards")
            val imdbID = jsonItem.getString("imdbID")
            val type = jsonItem.getString("Type")
            val posterURL = jsonItem.getString("Poster")
            val metascore = jsonItem.getString("Metascore")
            val imdbRating = jsonItem.getString("imdbRating")
            val film = Film(
                title,
                imdbID,
                year,
                rated,
                released,
                runtime,
                genre,
                director,
                actors,
                plot,
                language,
                country,
                awards,
                posterURL,
                metascore,
                imdbRating,
                type
            )
            Log.d(TAG, "Film item constructed: $film")

        } catch (e: JSONException) {
            e.printStackTrace()
            Log.e(TAG, ".createFilmFromJSON: Error processing JSON data. ${e.message}")
        }

        // return film ?
    }

    private fun createResultsFromJSON(result: JSONObject) { // should be returned somewhere
        Log.d(TAG, ".createResultsFromJSON starting")

        try {
            val itemsArray = result.getJSONArray("Search")
            for (i in 0 until itemsArray.length()) {
                val jsonItem = itemsArray.getJSONObject(i)
                val title = jsonItem.getString("Title")
                val year = jsonItem.getString("Year")
                val imdbID = jsonItem.getString("imdbID")
                val type = jsonItem.getString("Type")
                val posterURL = jsonItem.getString("Poster")
                val searchResult = Result(title, year, imdbID, type, posterURL)
                Log.d(TAG, "New search result item constructed: $searchResult")
                resultList.add(searchResult)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            Log.e(TAG, ".createResultsFromJSON: Error processing JSON data. ${e.message}")
        }
        // test call todo remove
        resultList.get(0).getMovieFromSearchResult()
    }

    inner class Result(
        val title: String,
        val year: String,
        val imdbID: String,
        val type: String,
        val posterURL: String
    ) {
        override fun toString(): String {
            return "Result(title='$title', year='$year', imdbID='$imdbID')"
        }

        fun getMovieFromSearchResult() {
            // Use the imdbID to retrieve more information about the specific search result
            searchByFilmID(imdbID)
        }
    } // Results are simple objects which don't provide as much information as Film objects. When we want to view more information about a film - for example when a user taps a result - we will have to do a search (likely by imdbID)

    fun searchByFilmID(imdbID: String) {
        val query = "?i=$imdbID" // i= Search by IMDB id.
        GetOMDBJsonData(this, apikey, ResultType.FILM_INFORMATION).execute(OMDBurl + query)
    }
}


