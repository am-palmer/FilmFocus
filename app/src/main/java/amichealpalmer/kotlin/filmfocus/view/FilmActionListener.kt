package amichealpalmer.kotlin.filmfocus.view

import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.model.entity.WatchlistItem

interface FilmActionListener {
    fun addFilmToWatchlist(film: FilmThumbnail)

    fun markFilmWatched(film: FilmThumbnail)

    fun removeFilmFromWatchlist(watchlistItem: WatchlistItem)

    fun showFilmDetails(film: FilmThumbnail)
}