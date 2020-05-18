package amichealpalmer.kotlin.filmfocus.viewmodel

import amichealpalmer.kotlin.filmfocus.model.entity.TimelineItem
import amichealpalmer.kotlin.filmfocus.model.room.TimelineItemRepository
import android.app.Application
import androidx.lifecycle.MutableLiveData

class TimelineItemViewModel(application: Application) {

    private val repository: TimelineItemRepository by lazy { TimelineItemRepository(application) }

    fun addUpdateItem(timelineItem: TimelineItem){
        repository.insertUpdate(timelineItem)
    }

    fun removeItem(timelineItem: TimelineItem){
        repository.delete(timelineItem)
    }

    fun clearTimeline(){
        repository.deleteAll()
    }

    fun getTimelineItemList(): MutableLiveData<ArrayList<TimelineItem>> {
        return repository.getTimelineItems
    }

}