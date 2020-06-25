package amichealpalmer.kotlin.filmfocus.viewmodel

import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.model.entity.TimelineItem
import amichealpalmer.kotlin.filmfocus.model.entity.WatchlistItem
import amichealpalmer.kotlin.filmfocus.model.remote.OMDBRepository
import amichealpalmer.kotlin.filmfocus.model.room.TimelineItemRepository
import amichealpalmer.kotlin.filmfocus.model.room.WatchlistItemRepository
import androidx.lifecycle.*
import kotlinx.coroutines.launch

// Holds data displayed in the Browse fragment
class BrowseViewModel internal constructor(private val repository: OMDBRepository,
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

    fun getHaveMoreResults(): MutableLiveData<Boolean>{
        return repository.getHaveMoreResults
    }

    fun getCurrentlyLoadingResults(): Boolean{
        return repository.getCurrentlyLoadingResults ?: false
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

        // Remove the film from the watchlist if it is present
        viewModelScope.launch {
            watchlistRepository.deleteByImdbId(timelineItem.film.imdbID)
        }

        viewModelScope.launch {
            // Add the film to the history
            timelineRepository.insertUpdate(timelineItem)
        }
    }

}

class BrowseViewModelFactory(private val repository: OMDBRepository,
                             private val watchlistRepository: WatchlistItemRepository,
                             private val timelineRepository: TimelineItemRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BrowseViewModel(repository, watchlistRepository, timelineRepository) as T
    }
}