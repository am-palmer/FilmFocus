package amichealpalmer.kotlin.filmfocus.viewmodel

import amichealpalmer.kotlin.filmfocus.model.entity.TimelineItem
import amichealpalmer.kotlin.filmfocus.model.entity.WatchlistItem
import amichealpalmer.kotlin.filmfocus.model.room.TimelineItemRepository
import amichealpalmer.kotlin.filmfocus.model.room.WatchlistItemRepository
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class WatchlistViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WatchlistItemRepository by lazy { WatchlistItemRepository(application) }
    private val timeline: TimelineItemRepository by lazy { TimelineItemRepository(application) }

//    fun addItem(film: FilmThumbnail) {
//        repository.insert(film)
//    }

    fun removeItem(watchlistItem: WatchlistItem) {
        repository.delete(watchlistItem)
    }

    fun getWatchlist(): LiveData<List<WatchlistItem>> {
        return repository.getWatchlist
    }

    fun clearWatchlist() {
        repository.deleteAll()
    }

    fun addItemToHistory(timelineItem: TimelineItem) {
        timeline.insertUpdate(timelineItem)
    }
}

class WatchlistViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return WatchlistViewModel(application) as T
    }
}