package amichealpalmer.kotlin.filmfocus.model.room

import amichealpalmer.kotlin.filmfocus.model.entity.TimelineItem
import amichealpalmer.kotlin.filmfocus.model.entity.WatchlistItem
import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData

// todo: coroutines

class TimelineItemRepository(application: Application) {

    private val timelineDao: TimelineItemDao by lazy { TimelineItemDatabase.getInstance(application)!!.timelineItemDao() }
    private val timelineList: LiveData<List<WatchlistItem>> by lazy { timelineDao.getAllTimelineItems() }

    // Exposed API functions for the rest of the app

    fun insertUpdate(timelineItem: TimelineItem): AsyncTask<TimelineItem, Unit, Unit>? {
        return InsertTimelineItemAsyncTask(timelineDao).execute(timelineItem)
    }

    fun delete(timelineItem: TimelineItem) {
        DeleteTimelineItemAsyncTask(timelineDao).execute(timelineItem)
    }

    fun deleteAll() {
        DeleteAllTimelineItemsAsyncTask(timelineDao).execute()
    }

    val getTimelineItems get() = timelineList

    // Async task objects handle database operations
    companion object {

        private class InsertTimelineItemAsyncTask(val timelineDao: TimelineItemDao) : AsyncTask<TimelineItem, Unit, Unit>() {
            override fun doInBackground(vararg params: TimelineItem?) {
                timelineDao.insertUpdate(params[0]!!)
            }
        }

        private class DeleteTimelineItemAsyncTask(val timelineDao: TimelineItemDao) : AsyncTask<TimelineItem, Unit, Unit>() {
            override fun doInBackground(vararg params: TimelineItem?) {
                timelineDao.delete(params[0]!!)
            }
        }

        private class DeleteAllTimelineItemsAsyncTask(val timelineDao: TimelineItemDao) : AsyncTask<Unit, Unit, Unit>() {
            override fun doInBackground(vararg params: Unit?) {
                timelineDao.deleteAllTimelineItems()
            }
        }


    }

}