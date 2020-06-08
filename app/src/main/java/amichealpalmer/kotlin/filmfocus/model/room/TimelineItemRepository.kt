package amichealpalmer.kotlin.filmfocus.model.room

import amichealpalmer.kotlin.filmfocus.model.entity.TimelineItem
import android.app.Application
import androidx.lifecycle.LiveData

// The exposed API functions used by the ViewModel(s)
class TimelineItemRepository(application: Application) {
    private val timelineDao: TimelineItemDao by lazy { TimelineItemDatabase.getInstance(application)!!.timelineItemDao() }

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

}