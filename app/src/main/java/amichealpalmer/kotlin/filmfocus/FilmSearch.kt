package amichealpalmer.kotlin.filmfocus

import android.util.Log


// Uses an IMDB id to get results about a film

class FilmSearch(val listener: FilmDetailsActivity) {

    private val TAG = "FilmSearch"

    fun getFilmByID(imdbID: String) {
        GetJSONFilm(this, listener.getString(R.string.OMDB_API_KEY)).execute(imdbID)
    }

    fun onFilmInfoDownloadComplete(film: Film) { // Called from our GetJSONFilm class once .doInBackground finishes executing
        Log.d(TAG, ".onFilmInfoDownloadComplete called.")
        Log.d(TAG, "FILM DATA: ${film}")
        Log.d(TAG, "passing object to listener")
        listener.onFilmInfoDownload(film)
    }
}