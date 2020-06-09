package amichealpalmer.kotlin.filmfocus.viewmodel

import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.model.entity.TimelineItem
import amichealpalmer.kotlin.filmfocus.model.room.TimelineItemDatabase
import amichealpalmer.kotlin.filmfocus.model.room.TimelineItemRepository
import amichealpalmer.kotlin.filmfocus.model.room.WatchlistItemDatabase
import amichealpalmer.kotlin.filmfocus.model.room.WatchlistItemRepository
import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch

class TimelineViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TimelineItemRepository by lazy {
        TimelineItemRepository.getInstance(TimelineItemDatabase.getInstance(application.applicationContext).timelineItemDao())
    }
    private val watchlist: WatchlistItemRepository by lazy {
        WatchlistItemRepository.getInstance(WatchlistItemDatabase.getInstance(application.applicationContext).watchlistItemDao())
    }

    fun addUpdateItem(timelineItem: TimelineItem) {
        viewModelScope.launch { repository.insertUpdate(timelineItem) }
    }

    fun removeItem(timelineItem: TimelineItem) {
        viewModelScope.launch { repository.delete(timelineItem) }
    }

    fun clearTimeline() {
        viewModelScope.launch { repository.deleteAll() }
    }

    fun getTimelineItemList(): LiveData<List<TimelineItem>> {
        return repository.getTimelineItems()
    }

    fun addItemToWatchlist(filmThumbnail: FilmThumbnail) {
        viewModelScope.launch { watchlist.insert(filmThumbnail) }
    }

}

class TimelineViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TimelineViewModel(application) as T
    }
}