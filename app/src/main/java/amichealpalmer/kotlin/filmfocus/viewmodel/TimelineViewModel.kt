package amichealpalmer.kotlin.filmfocus.viewmodel

import amichealpalmer.kotlin.filmfocus.model.entity.TimelineItem
import amichealpalmer.kotlin.filmfocus.model.room.TimelineItemRepository
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TimelineViewModel(application: Application): AndroidViewModel(application) {

    private val repository: TimelineItemRepository by lazy { TimelineItemRepository(application) }

    // todo: check we aren't reversing the list at the wrong time etc

    fun addUpdateItem(timelineItem: TimelineItem) {
        repository.insertUpdate(timelineItem)
    }

    fun removeItem(timelineItem: TimelineItem) {
        repository.delete(timelineItem)
    }

    fun clearTimeline() {
        repository.deleteAll()
    }

    fun getTimelineItemList(): LiveData<List<TimelineItem>> {
        return repository.getTimelineItems
    }

}

class TimelineViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TimelineViewModel(application) as T
    }
}