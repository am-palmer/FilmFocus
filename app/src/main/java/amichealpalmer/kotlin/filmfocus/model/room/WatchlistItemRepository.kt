package amichealpalmer.kotlin.filmfocus.model.room

import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.model.entity.WatchlistItem
import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.MutableLiveData

// todo: use coroutines

class WatchlistItemRepository(application: Application) {

    private val watchlistDao: WatchlistItemDao by lazy { WatchlistItemDatabase.getInstance(application)!!.watchlistItemDao() }
    private val watchlist: MutableLiveData<ArrayList<WatchlistItem>> by lazy { watchlistDao.getAllWatchlistItems() }

    // Exposed API functions for the rest of the app

    fun insert(filmThumbnail: FilmThumbnail): AsyncTask<WatchlistItem, Unit, Unit>? {
        val itemToInsert = WatchlistItem(filmThumbnail)
        return InsertWatchlistItemAsyncTask(
                watchlistDao
        ).execute(itemToInsert)
    }

    fun delete(watchlistItem: WatchlistItem) {
        DeleteWatchlistItemAsyncTask(watchlistDao).execute(watchlistItem)
    }

    fun deleteAll() {
        DeleteAllWatchlistItemsAsyncTask(watchlistDao).execute()
    }

    val getWatchlist get() = watchlist


    // AsyncTasks handle database operations on a different thread
    companion object {

        private class InsertWatchlistItemAsyncTask(val watchlistDao: WatchlistItemDao) : AsyncTask<WatchlistItem, Unit, Unit>() {
            override fun doInBackground(vararg params: WatchlistItem?) {
                watchlistDao.insert(params[0]!!)
            }
        }

        private class DeleteWatchlistItemAsyncTask(val watchlistDao: WatchlistItemDao) : AsyncTask<WatchlistItem, Unit, Unit>() {
            override fun doInBackground(vararg params: WatchlistItem?) {
                watchlistDao.delete(params[0]!!)
            }
        }

        private class DeleteAllWatchlistItemsAsyncTask(val watchlistDao: WatchlistItemDao) : AsyncTask<Unit, Unit, Unit>() {
            override fun doInBackground(vararg params: Unit?) {
                watchlistDao.deleteAllWatchlistItems()
            }
        }

    }


}