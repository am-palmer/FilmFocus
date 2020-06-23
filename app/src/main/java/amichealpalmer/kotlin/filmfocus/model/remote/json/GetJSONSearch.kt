package amichealpalmer.kotlin.filmfocus.model.remote.json

import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.model.remote.OMDBRepository
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference
// todo delete
// Retrieve JSON SearchResponse Data and return it to the calling class.
class GetJSONSearch(private val listener: WeakReference<OMDBRepository>, private val apikey: String) :
        GetJSONBase<ArrayList<FilmThumbnail?>>() { // Example input query is "?s=ghost". We then append the website and API key to form a valid URL (in the super class helper method)

    override fun onPostExecute(result: ArrayList<FilmThumbnail?>) { // Notify OMDBRepository, update LiveData object
        listener.get()?.updateResults(result)
    }

    override fun doInBackground(vararg params: String): ArrayList<FilmThumbnail?> { // params[0] should contain our query
        val defaultResult = ArrayList<FilmThumbnail?>()

        // Get our JSON object from the parent class
        val jsonResult = super.getJSONDataObject(apikey, params[0])

        return if (jsonResult != null) {
            createResultsFromJSON(jsonResult)
        } else {
            Log.d(TAG, "JSONResult is null")
            defaultResult
        }

    }

    private fun createResultsFromJSON(result: JSONObject): ArrayList<FilmThumbnail?> { // Parse JSON object and create an ArrayList of FilmThumbnails (if possible)
        val resultList = ArrayList<FilmThumbnail?>()

        // A bit magic number-ish, but result.length is 2 when there are no more results, from observation, 3 otherwise (due to the way the JSON is structured)
        if (result.length() == 2){
            return resultList // Empty, so the receiver knows the end of the results has been reached
        }

        try {
            val itemsArray = result.getJSONArray("SearchResponse")
            for (i in 0 until itemsArray.length()) {
                val jsonItem = itemsArray.getJSONObject(i)
                val title = jsonItem.getString("Title")
                val year = jsonItem.getString("Year")
                val imdbID = jsonItem.getString("imdbID")
                val type = jsonItem.getString("Type")
                val posterURL = jsonItem.getString("Poster")
                val searchResult = FilmThumbnail(title, year, imdbID, type, posterURL)
                resultList.add(searchResult)
            }
        } catch (e: JSONException) { // Thrown when there are no or malformed results (i.e. thrown when we reach the end of the result list)
            Log.d(TAG, ".createResultsFromJSON: Error processing JSON data. ${e.message}")
        }
        return resultList
    }

    companion object {
        private const val TAG = "GetJSONSearch"
    }

}