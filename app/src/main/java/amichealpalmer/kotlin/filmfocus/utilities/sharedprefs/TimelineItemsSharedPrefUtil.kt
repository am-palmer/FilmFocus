package amichealpalmer.kotlin.filmfocus.utilities.sharedprefs

import amichealpalmer.kotlin.filmfocus.model.TimelineItem
import amichealpalmer.kotlin.filmfocus.utilities.LocalDateSerializer
import android.content.Context
import android.util.Log
import com.google.gson.reflect.TypeToken
import org.joda.time.LocalDate
import java.lang.reflect.Type

// Used to load and save the items displayed in the History timeline in SharedPrefs. Note no reversing of list in here, the UI handles that
// todo: these could be singletons
class TimelineItemsSharedPrefUtil(context: Context) : BaseSharedPrefUtil(context) {

    private val TAG = "TimelineSharedPrefUt"
    private val SHAREDPREFS_KEY_TIMELINEITEMS = "timelineItems"

    init {
        gsonBuilder.registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
        gson = gsonBuilder.create() // Recreating here so we have the type adapter
    }

    // Returns either the timeline item array from SharedPrefs or creates new one if it does not exist yet
    fun loadTimelineItems(): ArrayList<TimelineItem> {
        Log.d(TAG, ".loadTimelineItems begins")
        val timelineJson = sharedPreferences.getString(SHAREDPREFS_KEY_TIMELINEITEMS, null)
        return if (timelineJson == null) {
            val timelineList = ArrayList<TimelineItem>()
            saveTimelineItems(timelineList)
            timelineList
        } else {
            val timelineType: Type = object : TypeToken<ArrayList<TimelineItem>>() {}.type
            gson.fromJson(timelineJson, timelineType) as ArrayList<TimelineItem>
        }
    }

    // Could return false in some unwanted case, but we allow multiple instances of the same film in the timeline, as a user might rewatch a film
    fun addItemToTimeline(item: TimelineItem): Boolean {
        val timelineList: ArrayList<TimelineItem>? = loadTimelineItems()
        if (timelineList != null) {
            timelineList.add(item)
            saveTimelineItems(timelineList)
            return true
        } else {
            Log.e(TAG, "call made to addItemToTimeline when the object hasn't been initialized in sharedprefs")
        }
        return false
    }

    // Called when user edits an item in the timeline
    fun updateTimelineItem(item: TimelineItem) {
        val timelineList = loadTimelineItems()
        if (timelineList != null) {
            for (i in timelineList) {
                if (i.date == item.date && i.film.imdbID == item.film.imdbID) {
                    val position = timelineList.indexOf(i)
                    timelineList[position] = item
                    saveTimelineItems(timelineList)
                }
            }
        }
    }

    // Return false if it doesn't exist - however should be impossible
    fun removeItemFromTimeline(item: TimelineItem): Boolean {
        val timelineList: ArrayList<TimelineItem>? = loadTimelineItems()
        if (timelineList != null) {
            for (i in timelineList) {
                if (i == item) {
                    timelineList.remove(i)
                    saveTimelineItems(timelineList)
                    return true
                }
            }
        } else {
            Log.e(TAG, "call made to removeItemFromTimeline when the object hasn't been initialized in sharedprefs")
        }
        return false
    }

    // Returns false if the timeline is already empty
    fun clearTimeline(): Boolean {
        val timeline = loadTimelineItems()
        if (timeline != null) {
            return if (timeline.size > 0) {
                timeline.clear()
                saveTimelineItems(timeline)
                true
            } else false
        } else {
            Log.e(TAG, "call made to clearTimeline when the object hasn't been initialized in sharedprefs")
        }
        return false
    }

    private fun saveTimelineItems(timelineList: ArrayList<TimelineItem>) {
        Log.d(TAG, ".saveTimelineItems begins")
        val editor = sharedPreferences.edit()
        val timelineJson = gson.toJson(timelineList)
        editor.putString(SHAREDPREFS_KEY_TIMELINEITEMS, timelineJson)
        editor.apply()
    }

}