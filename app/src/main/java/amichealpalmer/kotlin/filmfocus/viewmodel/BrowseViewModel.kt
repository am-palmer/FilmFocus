package amichealpalmer.kotlin.filmfocus.viewmodel

import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.model.entity.TimelineItem
import amichealpalmer.kotlin.filmfocus.model.entity.WatchlistItem
import amichealpalmer.kotlin.filmfocus.model.remote.FilmThumbnailRepository
import amichealpalmer.kotlin.filmfocus.model.room.TimelineItemRepository
import amichealpalmer.kotlin.filmfocus.model.room.WatchlistItemRepository
import androidx.lifecycle.*
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

    fun getHaveMoreResults(): MutableLiveData<Boolean>{
        return repository.getHaveMoreResults
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
// todo: not actually using this to its potential
class BrowseViewModelFactory(private val repository: FilmThumbnailRepository,
                             private val watchlistRepository: WatchlistItemRepository,
                             private val timelineRepository: TimelineItemRepository) : ViewModelProvider.Factory {

//    override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
//        return BrowseViewModel(repository, watchlistRepository, timelineRepository) as T
//    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BrowseViewModel(repository, watchlistRepository, timelineRepository) as T
    }
}