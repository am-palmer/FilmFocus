package amichealpalmer.kotlin.filmfocus.viewmodel

import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.model.entity.TimelineItem
import amichealpalmer.kotlin.filmfocus.model.entity.WatchlistItem
import amichealpalmer.kotlin.filmfocus.model.remote.FilmThumbnailRepository
import amichealpalmer.kotlin.filmfocus.model.room.TimelineItemRepository
import amichealpalmer.kotlin.filmfocus.model.room.WatchlistItemRepository
import android.os.Bundle
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.launch

// Holds data displayed in the Browse fragment
class BrowseViewModel internal constructor(private val repository: FilmThumbnailRepository,
                                           private val watchlistRepository: WatchlistItemRepository,
                                           private val timelineRepository: TimelineItemRepository) : ViewModel() {

    fun newQuery(query: String) {
        repository.newQuery(query)
    }

    fun nextPage() {
        repository.getNextPage()
    }

    fun getResults(): MutableLiveData<ArrayList<FilmThumbnail?>> {
        return repository.getResults
    }

    fun getQuery(): MutableLiveData<String?> {
        return repository.getQuery
    }

    fun getWatchlist(): LiveData<List<WatchlistItem>> {
        return watchlistRepository.getWatchlistItems()
    }

    fun addToWatchlist(filmThumbnail: FilmThumbnail) {
        viewModelScope.launch {
            watchlistRepository.insert(filmThumbnail)
        }
    }

    fun markWatched(timelineItem: TimelineItem) {
        viewModelScope.launch {
            timelineRepository.insertUpdate(timelineItem)
        }
    }

}

class BrowseViewModelFactory(private val repository: FilmThumbnailRepository,
                             private val watchlistRepository: WatchlistItemRepository,
                             private val timelineRepository: TimelineItemRepository,
                             owner: SavedStateRegistryOwner,
                             defaultArgs: Bundle? = null) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
        return BrowseViewModel(repository, watchlistRepository, timelineRepository) as T
    }

}