package amichealpalmer.kotlin.filmfocus.model.room

import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.model.entity.WatchlistItem
import android.app.Application
import androidx.lifecycle.LiveData

class WatchlistItemRepository(application: Application) {
    private val watchlistDao: WatchlistItemDao by lazy { WatchlistItemDatabase.getInstance(application)!!.watchlistItemDao() }

    suspend fun insert(filmThumbnail: FilmThumbnail) {
        val item = WatchlistItem(filmThumbnail)
        watchlistDao.insert(item)
    }

    suspend fun delete(watchlistItem: WatchlistItem) {
        watchlistDao.delete(watchlistItem)
    }

    suspend fun deleteAll() {
        watchlistDao.deleteAllWatchlistItems()
    }

    fun getWatchlistItems(): LiveData<List<WatchlistItem>> {
        return watchlistDao.getAllWatchlistItems()
    }

}