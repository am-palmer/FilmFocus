package amichealpalmer.kotlin.filmfocus.model.room

import amichealpalmer.kotlin.filmfocus.model.entity.TimelineItem
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TimelineItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpdate(timelineItem: TimelineItem)

    @Delete
    suspend fun delete(timelineItem: TimelineItem)

    @Query("DELETE FROM timeline")
    suspend fun deleteAllTimelineItems()

    // Get our LiveData object - note should NOT be a suspend function
    @Query("SELECT * FROM timeline")
    fun getAllTimelineItems(): LiveData<List<TimelineItem>>

}