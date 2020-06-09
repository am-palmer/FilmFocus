package amichealpalmer.kotlin.filmfocus.viewmodel

import amichealpalmer.kotlin.filmfocus.model.entity.TimelineItem
import amichealpalmer.kotlin.filmfocus.model.entity.WatchlistItem
import amichealpalmer.kotlin.filmfocus.model.room.TimelineItemDatabase
import amichealpalmer.kotlin.filmfocus.model.room.TimelineItemRepository
import amichealpalmer.kotlin.filmfocus.model.room.WatchlistItemDatabase
import amichealpalmer.kotlin.filmfocus.model.room.WatchlistItemRepository
import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch

class WatchlistViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: WatchlistItemRepository by lazy {
        WatchlistItemRepository.getInstance(WatchlistItemDatabase.getInstance(application.applicationContext).watchlistItemDao())
    }
    private val timeline: TimelineItemRepository by lazy {
        TimelineItemRepository.getInstance(TimelineItemDatabase.getInstance(application.applicationContext).timelineItemDao())
    }

    fun removeItem(watchlistItem: WatchlistItem) {
        viewModelScope.launch { repository.delete(watchlistItem) }
    }

    fun clearWatchlist() {
        viewModelScope.launch { repository.deleteAll() }
    }

    fun addItemToHistory(timelineItem: TimelineItem) {
        viewModelScope.launch { timeline.insertUpdate(timelineItem) }
    }

    fun getWatchlist(): LiveData<List<WatchlistItem>> {
        return repository.getWatchlistItems()
    }

    val TAG = "WatchlistViewModel"
}

class WatchlistViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return WatchlistViewModel(application) as T
    }
}