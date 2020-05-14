package amichealpalmer.kotlin.filmfocus.model.room

import amichealpalmer.kotlin.filmfocus.model.entity.WatchlistItem
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WatchlistItemDao {

    @Insert
    fun insert(watchlistItem: WatchlistItem)

    @Delete
    fun delete(watchlistItem: WatchlistItem)

    @Query("DELETE FROM watchlist")
    fun deleteAllWatchlistItems()

    // Get our LiveData object
    @Query("SELECT * FROM watchlist")
    fun getAllWatchlistItems(): LiveData<List<WatchlistItem>>

}
