package amichealpalmer.kotlin.filmfocus.utilities.sharedprefs

import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.model.TimelineItem
import android.content.Context
import android.util.Log
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

// Used to load and save the items displayed in the History timeline in SharedPrefs

class TimelineItemsSharedPrefUtil(context: Context) : BaseSharedPrefUtil(context) {

    private val TAG = "TimelineSharedPrefUt"
    private val SHAREDPREFS_KEY_TIMELINEITEMS = "timelineItems"

    // Returns either the timeline item array from SharedPrefs or null if it does not exist
    fun loadTimelineItems(): ArrayList<TimelineItem>? {
        Log.d(TAG, ".loadTimelineItems begins")
        val timelineJson = sharedPreferences.getString(SHAREDPREFS_KEY_TIMELINEITEMS, null)
        return if (timelineJson == null) {
            null
        } else {
            val timelineType: Type = object : TypeToken<ArrayList<TimelineItem>>() {}.type
            gson.fromJson(timelineJson, timelineType) as ArrayList<TimelineItem>
        }
    }

    fun saveTimelineItems(watchlist: ArrayList<FilmThumbnail>) {
        Log.d(TAG, ".saveTimelineItems begins")
        val editor = sharedPreferences.edit()
        val watchlistJson = gson.toJson(watchlist)
        editor.putString(SHAREDPREFS_KEY_TIMELINEITEMS, watchlistJson)
        editor.apply()
    }

}