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
    suspend fun insert(watchlistItem: WatchlistItem)

    @Delete
    suspend fun delete(watchlistItem: WatchlistItem)

    @Query("DELETE FROM watchlist WHERE imdbID = :imdbID")
    suspend fun deleteByImdbId(imdbID: String)

    @Query("DELETE FROM watchlist")
    suspend fun deleteAllWatchlistItems()

    // Get our LiveData object
    @Query("SELECT * FROM watchlist")
    fun getAllWatchlistItems(): LiveData<List<WatchlistItem>>

}
