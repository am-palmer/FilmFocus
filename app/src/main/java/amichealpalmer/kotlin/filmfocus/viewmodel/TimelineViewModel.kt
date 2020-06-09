package amichealpalmer.kotlin.filmfocus.viewmodel

import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.model.entity.TimelineItem
import amichealpalmer.kotlin.filmfocus.model.room.TimelineItemRepository
import amichealpalmer.kotlin.filmfocus.model.room.WatchlistItemRepository
import android.os.Bundle
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.launch

class TimelineViewModel internal constructor(private val repository: TimelineItemRepository,
                                             private val watchlistRepository: WatchlistItemRepository) : ViewModel() {

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
        viewModelScope.launch { watchlistRepository.insert(filmThumbnail) }
    }

}

class TimelineViewModelFactory(private val repository: TimelineItemRepository,
                               private val watchlistRepository: WatchlistItemRepository,
                               owner: SavedStateRegistryOwner,
                               defaultArgs: Bundle? = null) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
        return TimelineViewModel(repository, watchlistRepository) as T

    }
}