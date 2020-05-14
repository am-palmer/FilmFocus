package amichealpalmer.kotlin.filmfocus.model.room

import amichealpalmer.kotlin.filmfocus.model.entity.TimelineItem
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TimelineItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUpdate(timelineItem: TimelineItem)

    @Delete
    fun delete(timelineItem: TimelineItem)

    @Query("DELETE FROM timeline")
    fun deleteAllTimelineItems()

    // Get our LiveData object
    @Query("SELECT * FROM timeline")
    fun getAllTimelineItems(): LiveData<List<TimelineItem>>

}