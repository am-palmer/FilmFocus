package amichealpalmer.kotlin.filmfocus.utilities.sharedprefs

import amichealpalmer.kotlin.filmfocus.utilities.LocalDateSerializer
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import org.joda.time.LocalDate

// Template class for retreiving objects from Shared Preferences

abstract class BaseSharedPrefUtil(private val context: Context) {

    private val SHARED_PREFS = "sharedPrefs"
    protected val sharedPreferences: SharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
    protected val builder = GsonBuilder()

    init {
        // Allow us to load and save objects which make use of the LocalDate object
        builder.registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
    }

//    private fun loadData() {
//        //Log.d(TAG, ".loadData called, loading data from Shared Preferences")
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
//            //Log.d(TAG, ".loadData: watchlist could not be loaded / doesn't exist yet. Making a new watchlist")
//            ArrayList()
//        } else {
//            val watchlistRetrieved = gson.fromJson(watchlistJson, watchlistType) as ArrayList<FilmThumbnail>?
//           // Log.d(TAG, ".loadData: watchlist loaded. it contains ${watchlistRetrieved!!.size} items")
//            watchlistRetrieved
//        }
//
//        // Load the timeline items
//        val timelineJson = sharedPreferences.getString(SHAREDPREFS_KEY_TIMELINE, null)
//        val timelineType: Type = object : TypeToken<ArrayList<TimelineItem>>() {}.type
//        timelineList = if (timelineJson == null) {
//            //Log.d(TAG, ".loadData: timeline could not be loaded / does not exist yet. Making a new watchlist")
//            ArrayList()
//        } else {
//            val timelineRetrieved = gson.fromJson(timelineJson, timelineType) as ArrayList<TimelineItem>
//            //Log.d(TAG, ".loadData: timeline retrieved")
//            timelineRetrieved
//        }
//    }

}