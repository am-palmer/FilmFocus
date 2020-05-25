package amichealpalmer.kotlin.filmfocus.viewmodel

import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.model.entity.TimelineItem
import amichealpalmer.kotlin.filmfocus.model.room.TimelineItemRepository
import amichealpalmer.kotlin.filmfocus.model.room.WatchlistItemRepository
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TimelineViewModel(application: Application): AndroidViewModel(application) {

    private val repository: TimelineItemRepository by lazy { TimelineItemRepository(application) }
    private val watchlist: WatchlistItemRepository by lazy { WatchlistItemRepository(application) }

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

    fun addItemToWatchlist(filmThumbnail: FilmThumbnail){
        watchlist.insert(filmThumbnail)
    }

}

class TimelineViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TimelineViewModel(application) as T
    }
}