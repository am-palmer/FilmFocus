package amichealpalmer.kotlin.filmfocus.utilities.json

//import amichealpalmer.kotlin.filmfocus.activities.SearchActivity
import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.view.BrowseFragment
import org.json.JSONObject
import android.util.Log
import org.json.JSONException

// Retrieve OMDB JSON Search Data and return it to the calling class.

class GetJSONSearch(private val listener: BrowseFragment.SearchHelper, private val apikey: String) :
        GetJSONBase<ArrayList<FilmThumbnail?>>() { // Example input query is "?s=ghost". We then append the website and API key to form a valid URL (in the super class helper method)

    private val TAG = "GetJSONSearch"
//    private var pageCount = 1
//    private var currentPage = 1

    override fun onPostExecute(result: ArrayList<FilmThumbnail?>) {
        Log.d(TAG, ".onPostExecute starts")
        listener.onSearchResultsDownload(result)
    }

    private fun createResultsFromJSON(result: JSONObject): ArrayList<FilmThumbnail?> { // JSONObject is turned into an ArrayList<Result>
        Log.d(TAG, ".createResultsFromJSON starting with raw input JSON data")
        val resultList = ArrayList<FilmThumbnail?>()
//        try {
//            return resultList
//        } catch (e: JSONException){
//            Log.d(TAG, ".")
//        }
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
        } catch (e: JSONException) { // Todo: this exception is currently always thrown when we reach the end of the results (when we hit a page number that contains no film items). Is this idiomatic use of exceptions?
            //d.printStackTrace()
            Log.d(TAG, ".createResultsFromJSON: Error processing JSON data. ${e.message}")
        }
        return resultList
    }

    override fun doInBackground(vararg params: String): ArrayList<FilmThumbnail?> { // params[0] should contain our query
        Log.d(TAG, ".doInBackground started")
        val defaultResult = ArrayList<FilmThumbnail?>() // todo better handling of nullability

        // Get our JSON object from the parent class
        Log.d(TAG, "calling super.getJSONDataObject and passing our search query")
        val JSONResult = super.getJSONDataObject(apikey, params[0])

        if (JSONResult != null) {
            Log.d(TAG, "JSONResult not null")
            return createResultsFromJSON(JSONResult)
        } else {
            Log.d(TAG, "JSONResult is null")
            return defaultResult
        }

    }

}