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

// Retrieve OMDB JSON Data and return it to the calling class.

enum class DownloadStatus { // Track status of download
    OK, IDLE, NOT_INITIALIZED, FAILED_OR_EMPTY, PERMISSIONS_ERROR, ERROR
}

enum class ResultType { // The possible return JSON structures we expect
    SEARCH_RESULT_LIST, FILM_INFORMATION
}

class GetOMDBJsonData(val listener: Search, val apikey: String, val resultType: ResultType) :
    AsyncTask<String, Void, JSONObject?>() { // todo: singleton pattern?
    // Example input query is "https://www.omdbapi.com/?s=ghost". We then append the API key to form a valid URL

    private val TAG = "GetOMDBJsonData"

    private var downloadStatus = DownloadStatus.IDLE

    override fun onPostExecute(result: JSONObject?) {
        Log.d(TAG, ".onPostExecute called, returning result to listener ${listener.toString()}")
        listener.onJSONDownloadComplete(result, resultType) // Pass the JSONObject? back to the listener (Search)
    }

    override fun doInBackground(vararg params: String?): JSONObject? {
        Log.d(TAG, ".doInBackground started")
        if (params[0] == null) {
            downloadStatus = DownloadStatus.NOT_INITIALIZED
            Log.e(TAG, "No URL specified?")
            return null
        }

//        val APIKEY: String?
//        try {
//           // APIKEY = System.getenv("OMDB_API_KEY") // Currently fetched from the system env.
//            // Todo: stronger api key protections
//            val APIKEY = R.string.OMDB_API_KEY
//            Log.d(TAG, "Fetched API Key: $APIKEY")
//        } catch (e: NullPointerException) {
//            downloadStatus = DownloadStatus.PERMISSIONS_ERROR
//            Log.e(TAG, "Failed to retreive OMDB API key environment variable")
//            return null
//        }

        val searchURL = params[0] + "&apikey=${apikey}"
        var rawData: String
        // Attempt to retreive raw data and store as String object
        try {
            downloadStatus = DownloadStatus.OK
            rawData = URL(searchURL).readText()
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is MalformedURLException -> {
                    downloadStatus = DownloadStatus.NOT_INITIALIZED
                    ".doInBackground: URL Malformed: ${e.message}"
                }
                is IOException -> {
                    downloadStatus = DownloadStatus.FAILED_OR_EMPTY
                    ".doInBackground: Encountered an IO Exception: ${e.message}"
                }
                is SecurityException -> {
                    downloadStatus = DownloadStatus.PERMISSIONS_ERROR
                    ".doInBackground: Security exception - no permission? ${e.message}"
                }
                else -> {
                    downloadStatus = DownloadStatus.ERROR
                    ".doInBackground: Unknown error retreiving raw data: ${e.message}"

                }
            }
            Log.e(TAG, errorMessage)
            return null
        }

        // Raw String object is used to create a JSON object
        var jsonData = JSONObject()
        try {
            jsonData = JSONObject(rawData)
        } catch (e: JSONException) {
            e.printStackTrace()
            Log.e(TAG, ".doInBackground: Error processing JSON data.")
            cancel(true)
            // call to the listener .onerror(e)
        }

        Log.d(TAG, ".doInBackground finished")
        return jsonData

    }
    //val string = R.string.OMDB_API_KEY

}