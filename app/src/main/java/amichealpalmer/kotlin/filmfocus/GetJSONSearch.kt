package amichealpalmer.kotlin.filmfocus

import org.json.JSONObject
import amichealpalmer.kotlin.filmfocus.R.*
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import org.json.JSONException
import java.io.IOException
import java.lang.Exception
import java.lang.NullPointerException
import java.net.MalformedURLException
import java.net.URL

// Retrieve OMDB JSON Search Data and return it to the calling class.

class GetJSONSearch(val listener: Search, val apikey: String) :
    GetJSONBase<ArrayList<GetJSONSearch.Result?>>() { // Example input query is "?s=ghost". We then append the website and API key to form a valid URL (in the super class helper method)

    private val TAG = "GetJSONSearch"


    override fun onPostExecute(result: ArrayList<Result?>) {
        Log.d(TAG, ".onPostExecute starts")
        listener.onResultListDownloadComplete(result)
    }

    private fun createResultsFromJSON(result: JSONObject): ArrayList<Result?> { // JSONObject is turned into an ArrayList<Result>
        Log.d(TAG, ".createResultsFromJSON starting with raw input JSON data")
        var resultList = ArrayList<Result?>()
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
        return resultList
    }

    inner class Result( // Simple objects holding data for each search result
        val title: String,
        val year: String,
        val imdbID: String,
        val type: String,
        val posterURL: String
    ) {
        override fun toString(): String {
            return "Result(title='$title', year='$year', imdbID='$imdbID')"
        }
    }

    override fun doInBackground(vararg params: String): ArrayList<Result?> { // params[0] should contain our query
        Log.d(TAG, ".doInBackground started")
        var defaultResult = ArrayList<Result?>() // todo better handling of nullability

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

//        Log.d(TAG, ".doInBackground finished")
//        return resultList

    }

}