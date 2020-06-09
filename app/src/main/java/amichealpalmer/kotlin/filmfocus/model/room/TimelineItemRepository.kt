package amichealpalmer.kotlin.filmfocus.model.room

import amichealpalmer.kotlin.filmfocus.model.entity.TimelineItem
import androidx.lifecycle.LiveData

// The exposed API functions used by the ViewModel(s)
class TimelineItemRepository private constructor(private val timelineDao: TimelineItemDao) {

    suspend fun insertUpdate(timelineItem: TimelineItem) {
        timelineDao.insertUpdate(timelineItem)
    }

    suspend fun delete(timelineItem: TimelineItem) {
        timelineDao.delete(timelineItem)
    }

    suspend fun deleteAll() {
        timelineDao.deleteAllTimelineItems()
    }

    fun getTimelineItems(): LiveData<List<TimelineItem>> {
        return timelineDao.getAllTimelineItems()
    }

    companion object {

        @Volatile
        private var instance: TimelineItemRepository? = null

        fun getInstance(timelineDao: TimelineItemDao) =
                instance ?: synchronized(this) {
                    instance ?: TimelineItemRepository(timelineDao).also { instance = it }
                }
    }

}