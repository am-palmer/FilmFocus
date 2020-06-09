package amichealpalmer.kotlin.filmfocus.viewmodel

import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.model.entity.TimelineItem
import amichealpalmer.kotlin.filmfocus.model.entity.WatchlistItem
import amichealpalmer.kotlin.filmfocus.model.remote.FilmThumbnailRepository
import amichealpalmer.kotlin.filmfocus.model.room.TimelineItemDatabase
import amichealpalmer.kotlin.filmfocus.model.room.TimelineItemRepository
import amichealpalmer.kotlin.filmfocus.model.room.WatchlistItemDatabase
import amichealpalmer.kotlin.filmfocus.model.room.WatchlistItemRepository
import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch

// Holds data displayed in the Browse fragment
class BrowseViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FilmThumbnailRepository by lazy { FilmThumbnailRepository(application) }
    private val watchlist: WatchlistItemRepository by lazy {
        WatchlistItemRepository.getInstance(WatchlistItemDatabase.getInstance(application.applicationContext).watchlistItemDao())
    }
    private val timeline: TimelineItemRepository by lazy {
        TimelineItemRepository.getInstance(TimelineItemDatabase.getInstance(application.applicationContext).timelineItemDao())
    }

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
        return watchlist.getWatchlistItems()
    }

    fun addToWatchlist(filmThumbnail: FilmThumbnail) {
        viewModelScope.launch { watchlist.insert(filmThumbnail) }
    }

    fun markWatched(timelineItem: TimelineItem) {
        viewModelScope.launch { timeline.insertUpdate(timelineItem) }
    }

}

class BrowseViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BrowseViewModel(application) as T
    }
}