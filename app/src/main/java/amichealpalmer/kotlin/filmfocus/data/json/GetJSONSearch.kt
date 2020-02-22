package amichealpalmer.kotlin.filmfocus.data.json

import amichealpalmer.kotlin.filmfocus.activities.SearchActivity
import amichealpalmer.kotlin.filmfocus.data.FilmThumbnail
import org.json.JSONObject
import android.util.Log
import org.json.JSONException

// Retrieve OMDB JSON Search Data and return it to the calling class.

class GetJSONSearch(val listener: SearchActivity, val apikey: String) :
        GetJSONBase<ArrayList<FilmThumbnail?>>() { // Example input query is "?s=ghost". We then append the website and API key to form a valid URL (in the super class helper method)

    private val TAG = "GetJSONSearch"

    override fun onPostExecute(result: ArrayList<FilmThumbnail?>) {
        Log.d(TAG, ".onPostExecute starts")
        listener.displaySearchResults(result)
    }

    private fun createResultsFromJSON(result: JSONObject): ArrayList<FilmThumbnail?> { // JSONObject is turned into an ArrayList<Result>
        Log.d(TAG, ".createResultsFromJSON starting with raw input JSON data")
        var resultList = ArrayList<FilmThumbnail?>()
        try {
            val itemsArray = result.getJSONArray("Search")
            for (i in 0 until itemsArray.length()) {
                val jsonItem = itemsArray.getJSONObject(i)
                val title = jsonItem.getString("Title")
                val year = jsonItem.getString("Year")
                val imdbID = jsonItem.getString("imdbID")
                val type = jsonItem.getString("Type")
                val posterURL = jsonItem.getString("Poster")
                val searchResult = FilmThumbnail(title, year, imdbID, type, posterURL)
                Log.d(TAG, "New search result item constructed: $searchResult")
                resultList.add(searchResult)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            Log.e(TAG, ".createResultsFromJSON: Error processing JSON data. ${e.message}")
        }
        return resultList
    }

    override fun doInBackground(vararg params: String): ArrayList<FilmThumbnail?> { // params[0] should contain our query
        Log.d(TAG, ".doInBackground started")
        var defaultResult = ArrayList<FilmThumbnail?>() // todo better handling of nullability

        // Get our JSON object from the parent class
        Log.d(TAG, "calling super.getJSONDataObject and passing our search query")
        var JSONResult = super.getJSONDataObject(apikey, params[0])

        if (JSONResult != null) {
            Log.d(TAG, "JSONResult not null")
            return createResultsFromJSON(JSONResult)
        } else {
            Log.d(TAG, "JSONResult is null")
            return defaultResult
        }

    }

}