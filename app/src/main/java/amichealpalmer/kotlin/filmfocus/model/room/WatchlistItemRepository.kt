package amichealpalmer.kotlin.filmfocus.model.room

import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.model.entity.WatchlistItem

class WatchlistItemRepository private constructor(private val watchlistDao: WatchlistItemDao) {

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

    suspend fun deleteByImdbId(imdbID: String){
        watchlistDao.deleteByImdbId(imdbID)
    }

    fun getWatchlistItems() = watchlistDao.getAllWatchlistItems()

    companion object {

        @Volatile
        private var instance: WatchlistItemRepository? = null

        fun getInstance(watchlistDao: WatchlistItemDao) =
                instance ?: synchronized(this) {
                    instance ?: WatchlistItemRepository(watchlistDao).also { instance = it }
                }
    }

}