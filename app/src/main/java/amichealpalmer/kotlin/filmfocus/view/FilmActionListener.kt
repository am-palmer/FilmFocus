package amichealpalmer.kotlin.filmfocus.view

import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail

interface FilmActionListener {
    fun addFilmToWatchlist(film: FilmThumbnail)

    fun markFilmWatched(film: FilmThumbnail)

    fun removeFilmFromWatchlist(film: FilmThumbnail)

    fun showFilmDetails(film: FilmThumbnail)
}