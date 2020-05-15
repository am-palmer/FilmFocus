package amichealpalmer.kotlin.filmfocus.model.remote.json

import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.view.BrowseFragment
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference

// Retrieve OMDB JSON Search Data and return it to the calling class.
class GetJSONSearch(private var listener: WeakReference<BrowseFragment.SearchHelper>, private val apikey: String) :
        GetJSONBase<ArrayList<FilmThumbnail?>>() { // Example input query is "?s=ghost". We then append the website and API key to form a valid URL (in the super class helper method)

    override fun onPostExecute(result: ArrayList<FilmThumbnail?>) {
        Log.d(TAG, ".onPostExecute starts")
        listener.get()?.onSearchResultsDownload(result)
    }

    private fun createResultsFromJSON(result: JSONObject): ArrayList<FilmThumbnail?> { // JSONObject is turned into an ArrayList<Result>
        Log.d(TAG, ".createResultsFromJSON starting with raw input JSON data")
        val resultList = ArrayList<FilmThumbnail?>()

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
            Log.d(TAG, ".createResultsFromJSON: Error processing JSON data. ${e.message}")
        }
        return resultList
    }

    override fun doInBackground(vararg params: String): ArrayList<FilmThumbnail?> { // params[0] should contain our query
        Log.d(TAG, ".doInBackground started")
        val defaultResult = ArrayList<FilmThumbnail?>()

        // Get our JSON object from the parent class
        Log.d(TAG, "calling super.getJSONDataObject and passing our search query")
        val jsonResult = super.getJSONDataObject(apikey, params[0])

        return if (jsonResult != null) {
            Log.d(TAG, "JSONResult not null")
            createResultsFromJSON(jsonResult)
        } else {
            Log.d(TAG, "JSONResult is null")
            defaultResult
        }

    }

    companion object{
        private const val TAG = "GetJSONSearch"
    }

}