package amichealpalmer.kotlin.filmfocus.utilities.sharedprefs

import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import android.content.Context
import android.util.Log
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

// Used for loading and saving the watchlist in sharedPrefs

class WatchlistSharedPrefUtil(context: Context) : BaseSharedPrefUtil(context) {

    private val TAG = "WatchlistSharedPrefUt"
    private val SHAREDPREFS_KEY_WATCHLIST = "watchlist"

    // Returns either the watchlist from SharedPrefs or null if it does not exist
    fun loadWatchlist(): ArrayList<FilmThumbnail>? {
        Log.d(TAG, ".loadWatchlist begins")
        val watchlistJson = sharedPreferences.getString(SHAREDPREFS_KEY_WATCHLIST, null)
        return if (watchlistJson == null) {
            null
        } else {
            val watchlistType: Type = object : TypeToken<ArrayList<FilmThumbnail>>() {}.type
            gson.fromJson(watchlistJson, watchlistType) as ArrayList<FilmThumbnail>
        }
    }

    fun saveWatchlist(watchlist: ArrayList<FilmThumbnail>) {
        Log.d(TAG, ".saveWatchlist begins")
        val editor = sharedPreferences.edit()
        val watchlistJson = gson.toJson(watchlist)
        editor.putString(SHAREDPREFS_KEY_WATCHLIST, watchlistJson)

        editor.apply()

    }

}