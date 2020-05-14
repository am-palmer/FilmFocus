package amichealpalmer.kotlin.filmfocus.util.json

import android.os.AsyncTask
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL

abstract class GetJSONBase<T> : AsyncTask<String, Void, T>() {

    private var downloadStatus = DownloadStatus.NOT_INITIALIZED

    enum class DownloadStatus { // Track status of download
        OK, NOT_INITIALIZED, FAILED_OR_EMPTY, PERMISSIONS_ERROR, ERROR
    }

    abstract override fun onPostExecute(result: T)

    abstract override fun doInBackground(vararg params: String): T

    fun getJSONDataObject(apikey: String, query: String): JSONObject? { // Helper method getting JSON data for a query todo improve handling of exceptions so we aren't just flinging null objects around
        // Todo: handle errors better

        val searchURL = OMDB_URL + query + "&apikey=${apikey}"
        val rawData: String?

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
                    ".doInBackground: Unknown error retrieving raw data: ${e.message}"

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
        }

        return jsonData

    }

    companion object {
        private const val TAG = "GetJSONBase"
        private const val OMDB_URL = "https://www.omdbapi.com/"
    }
}