package amichealpalmer.kotlin.filmfocus.model.room

import amichealpalmer.kotlin.filmfocus.model.entity.TimelineItem
import amichealpalmer.kotlin.filmfocus.util.FilmThumbnailTypeConverter
import amichealpalmer.kotlin.filmfocus.util.LocalDateTypeConverter
import amichealpalmer.kotlin.filmfocus.util.TimelineItemStatusTypeConverter
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(entities = [TimelineItem::class], version = 1)
@TypeConverters(LocalDateTypeConverter::class, TimelineItemStatusTypeConverter::class, FilmThumbnailTypeConverter::class)
abstract class TimelineItemDatabase : RoomDatabase() {

    abstract fun timelineItemDao(): TimelineItemDao

    companion object {

        private var instance: TimelineItemDatabase? = null

        fun getInstance(context: Context): TimelineItemDatabase? {
            if (instance == null) {
                synchronized(TimelineItemDatabase::class) {
                    instance = Room.databaseBuilder(context.applicationContext,
                            TimelineItemDatabase::class.java,
                            "timeline_database").fallbackToDestructiveMigration().build()
                }
            }
            return instance
        }

        fun destroyInstance() {
            instance = null
        }


    }

}