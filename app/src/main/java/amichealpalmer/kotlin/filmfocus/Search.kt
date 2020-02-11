package amichealpalmer.kotlin.filmfocus

import org.json.JSONObject

class Search() { // only supports search by title right now TODO: implement search by other values i.e. search by actor or director (if omdb supports it)
    // OMDB returns a JSON set of results containing the search string (by title)

    //https://www.omdbapi.com/?s=ghost&apikey=d0231b9b
    // simplify this later. there's probably a better way to do it


    // make a call to the api with a generic class which returns us the JSON data

    fun performSearch(titleContains: String) {
        val query = "?s=$titleContains" // Indicates search by title
        val OMDBurl = "https://www.omdbapi.com/"
        val getOMDBJsonData = GetOMDBJsonData(this).execute(OMDBurl + query)
    }

    fun onJSONDownloadComplete(result: JSONObject?) {
        if (result == null) {
            // ? throw toys out of cot
        } else {
            // parse json object and make result objects
        }


    }

    private class Result(
        val title: String,
        val year: String,
        val imdbID: String,
        val type: String,
        val posterURL: String
    ) // Results are simple objects which don't provide as much information as Film objects. When we want to view more information about a film - for example when a user taps a result - we will have to do a search (likely by imdbID)


}


