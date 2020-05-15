package amichealpalmer.kotlin.filmfocus.util

import amichealpalmer.kotlin.filmfocus.model.entity.TIMELINE_ITEM_STATUS
import androidx.room.TypeConverter

// Used by Room for storing the Dropped/Watched status of an item in the timeline

object TimelineItemStatusTypeConverter {

    @TypeConverter
    @JvmStatic
    fun statusToString(status: TIMELINE_ITEM_STATUS): String {
        return status.name
    }

    @TypeConverter
    @JvmStatic
    fun toStatus(string: String): TIMELINE_ITEM_STATUS {
        return TIMELINE_ITEM_STATUS.valueOf(string)
    }

}