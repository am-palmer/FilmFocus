package amichealpalmer.kotlin.filmfocus.viewmodel

import amichealpalmer.kotlin.filmfocus.model.entity.TimelineItem
import amichealpalmer.kotlin.filmfocus.model.entity.WatchlistItem
import amichealpalmer.kotlin.filmfocus.model.room.TimelineItemRepository
import amichealpalmer.kotlin.filmfocus.model.room.WatchlistItemRepository
import android.os.Bundle
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.launch

class WatchlistViewModel(private val repository: WatchlistItemRepository,
                         private val timelineItemRepository: TimelineItemRepository) : ViewModel() {

    fun removeItem(watchlistItem: WatchlistItem) {
        viewModelScope.launch { repository.delete(watchlistItem) }
    }

    fun clearWatchlist() {
        viewModelScope.launch { repository.deleteAll() }
    }

    fun addItemToHistory(timelineItem: TimelineItem) {
        viewModelScope.launch { timelineItemRepository.insertUpdate(timelineItem) }
    }

    fun getWatchlist(): LiveData<List<WatchlistItem>> {
        return repository.getWatchlistItems()
    }

    val TAG = "WatchlistViewModel"
}

class WatchlistViewModelFactory(private val repository: WatchlistItemRepository,
                                private val timelineItemRepository: TimelineItemRepository,
                                owner: SavedStateRegistryOwner,
                                defaultArgs: Bundle? = null) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
        return WatchlistViewModel(repository, timelineItemRepository) as T
    }
}