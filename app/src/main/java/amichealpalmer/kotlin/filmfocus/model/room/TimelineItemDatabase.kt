package amichealpalmer.kotlin.filmfocus.model.room

import amichealpalmer.kotlin.filmfocus.model.entity.TimelineItem
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [TimelineItem::class], version = 1)
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