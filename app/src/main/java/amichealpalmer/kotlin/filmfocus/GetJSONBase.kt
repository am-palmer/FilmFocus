package amichealpalmer.kotlin.filmfocus

import android.os.AsyncTask
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import java.net.MalformedURLException
import java.net.URL

abstract class GetJSONBase<T> : AsyncTask<String, Void, T>() {

    private val TAG = "GetJSONBase"
    private var downloadStatus = DownloadStatus.NOT_INITIALIZED

    enum class DownloadStatus { // Track status of download
        OK, IDLE, NOT_INITIALIZED, FAILED_OR_EMPTY, PERMISSIONS_ERROR, ERROR
    }

    abstract override fun onPostExecute(result: T)

    abstract override fun doInBackground(vararg params: String): T

    fun getJSONDataObject(apikey: String, query: String): JSONObject? { // Helper method getting JSON data for a query todo improve handling of exceptions so we aren't just flinging null objects around
        val omdbUrl = "https://www.omdbapi.com/" // make this an xml resource?
        Log.d(TAG, ".doInBackground started")
        // Todo: handle errors better
        // val defaultList = ArrayList<GetJSONSearch.Result?>() // Not a good solution...

        val searchURL = omdbUrl + query + "&apikey=${apikey}"
        var rawData: String?

        // Attempt to retrieve raw data and store as String object

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
        var jsonData: JSONObject? = null
        try {
            jsonData = JSONObject(rawData)
        } catch (e: JSONException) {
            e.printStackTrace()
            Log.e(TAG, ".doInBackground: Error processing JSON data. Null object being returned.")
            //return null
            // cancel(true)
            // call to the listener .onerror(e)
        }

        return jsonData

    }
}