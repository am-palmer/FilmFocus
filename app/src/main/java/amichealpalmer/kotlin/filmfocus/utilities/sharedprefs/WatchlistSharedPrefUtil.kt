package amichealpalmer.kotlin.filmfocus.utilities.sharedprefs

import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import android.content.Context
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

// Used for loading and saving the watchlist in sharedPrefs

class WatchlistSharedPrefUtil(context: Context) : BaseSharedPrefUtil(context) {

    private val TAG = "WatchlistStoreUtility"
    private val SHAREDPREFS_KEY_WATCHLIST = "watchlist"

    fun loadWatchlist(): ArrayList<FilmThumbnail>? {
        val gson = builder.create()
        val watchlistJson = sharedPreferences.getString(SHAREDPREFS_KEY_WATCHLIST, null)
        val watchlistType: Type = object : TypeToken<ArrayList<FilmThumbnail>>() {}.type
        return if (watchlistJson == null){
            null
        } else {
            gson.fromJson(watchlistJson, watchlistType) as ArrayList<FilmThumbnail>
        }
    }

    fun saveWatchlist(watchlist: ArrayList<FilmThumbnail>) {

    }

//    private fun loadData() {
//        Log.d(TAG, ".loadData called, loading data from Shared Preferences")
//        val sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
//        val builder = GsonBuilder()
//        builder.registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
//        // Watchlist
//        val gson = builder.create()
//
//        // Load the watchlist
//        val watchlistJson = sharedPreferences.getString(SHAREDPREFS_KEY_WATCHLIST, null)
//        val watchlistType: Type = object : TypeToken<ArrayList<FilmThumbnail>>() {}.type
//        watchlist = if (watchlistJson == null) { // Build a new watchlist
//            Log.d(TAG, ".loadData: watchlist could not be loaded / doesn't exist yet. Making a new watchlist")
//            ArrayList()
//        } else {
//            val watchlistRetrieved = gson.fromJson(watchlistJson, watchlistType) as ArrayList<FilmThumbnail>?
//            Log.d(TAG, ".loadData: watchlist loaded. it contains ${watchlistRetrieved!!.size} items")
//            watchlistRetrieved
//        }
//
//        // Load the timeline items
//        val timelineJson = sharedPreferences.getString(SHAREDPREFS_KEY_TIMELINE, null)
//        val timelineType: Type = object : TypeToken<ArrayList<TimelineItem>>() {}.type
//        timelineList = if (timelineJson == null) {
//            Log.d(TAG, ".loadData: timeline could not be loaded / does not exist yet. Making a new watchlist")
//            ArrayList()
//        } else {
//            val timelineRetrieved = gson.fromJson(timelineJson, timelineType) as ArrayList<TimelineItem>
//            Log.d(TAG, ".loadData: timeline retrieved")
//            timelineRetrieved
//        }
//    }

}