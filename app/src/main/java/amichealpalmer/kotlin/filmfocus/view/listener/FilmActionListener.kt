package amichealpalmer.kotlin.filmfocus.view.listener

import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.model.entity.WatchlistItem

// Todo: Refactor / streamline

interface BrowseActionListener{
    fun addFilmToWatchlist(film: FilmThumbnail)

    fun markFilmWatched(film: FilmThumbnail)

    fun showFilmDetails(film: FilmThumbnail)
}

interface WatchlistActionListener{
    fun markFilmWatched(film: FilmThumbnail)

    fun removeFilmFromWatchlist(watchlistItem: WatchlistItem)

    fun showFilmDetails(film: FilmThumbnail)
}

interface HistoryActionListener{
    fun addFilmToWatchlist(film: FilmThumbnail)

    fun showFilmDetails(film: FilmThumbnail)
}