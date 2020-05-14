package amichealpalmer.kotlin.filmfocus.model.room

import amichealpalmer.kotlin.filmfocus.model.entity.WatchlistItem
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [WatchlistItem::class], version = 1)
abstract class WatchlistItemDatabase : RoomDatabase() {

    abstract fun WatchlistItemDao(): WatchlistItemDao

    companion object {
        private var instance: WatchlistItemDatabase? = null

        fun getInstance(context: Context): WatchlistItemDatabase? {
            if (instance == null) {
                synchronized(WatchlistItemDatabase::class) {
                    instance = Room.databaseBuilder(context.applicationContext, WatchlistItemDatabase::class.java, "watchlist_database")
                            .fallbackToDestructiveMigration()
                            .build()
                }
            }
            return instance
        }

        fun destroyInstance() {
            instance = null
        }


    }

}