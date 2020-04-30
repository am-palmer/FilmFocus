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

    // Returns either the saved watchlist from SharedPrefs or creates one if it does not exist
    fun loadWatchlist(): ArrayList<FilmThumbnail> {
        Log.d(TAG, ".loadWatchlist begins")
        val watchlistJson = sharedPreferences.getString(SHAREDPREFS_KEY_WATCHLIST, null)
        return if (watchlistJson == null) {
            val watchlist = ArrayList<FilmThumbnail>()
            saveWatchlist(watchlist)
            watchlist
        } else {
            val watchlistType: Type = object : TypeToken<ArrayList<FilmThumbnail>>() {}.type
            gson.fromJson(watchlistJson, watchlistType) as ArrayList<FilmThumbnail>
        }
    }


    // Returns false if film is not in watchlist
    fun removeFilmFromWatchlist(film: FilmThumbnail): Boolean {
        val watchlist: ArrayList<FilmThumbnail>? = loadWatchlist()
        if (watchlist != null) {
            for (f in watchlist) {
                if (f.imdbID == film.imdbID) {
                    watchlist.remove(f)
                    saveWatchlist(watchlist)
                    return true
                }
            }
            return false
        } else {
            Log.e(TAG, "call made to removeFilmFromWatchlist when the object hasn't been initialized in sharedprefs")
        }
        return false
    }

    // Returns false if film is already in watchlist
    fun addFilmToWatchlist(film: FilmThumbnail): Boolean {
        val watchlist: ArrayList<FilmThumbnail>? = loadWatchlist()
        if (watchlist != null) {
            for (f in watchlist) {
                if (f.imdbID == film.imdbID) {
                    return false
                }
            }
            watchlist.add(film)
            saveWatchlist(watchlist)
            return true
        } else {
            Log.e(TAG, "call made to addFilmToWatchlist when the object hasn't been initialized in sharedprefs")
        }
        return false
    }


    // Returns false if already empty
    fun clearWatchlist(): Boolean {
        val watchlist = loadWatchlist()
        if (watchlist != null) {
            return if (watchlist.size > 0) {
                watchlist.clear()
                saveWatchlist(watchlist)
                true
            } else {
                false
            }
        } else {
            Log.e(TAG, "call made to clearWatchlist when object is not initialized in sharedPrefs")
        }
        return false
    }

    private fun saveWatchlist(watchlist: ArrayList<FilmThumbnail>) {
        Log.d(TAG, ".saveWatchlist begins")
        val editor = sharedPreferences.edit()
        val watchlistJson = gson.toJson(watchlist)
        editor.putString(SHAREDPREFS_KEY_WATCHLIST, watchlistJson)
        editor.apply()
    }

}