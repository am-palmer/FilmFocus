package amichealpalmer.kotlin.filmfocus.util

import amichealpalmer.kotlin.filmfocus.model.remote.FilmThumbnailRepository
import amichealpalmer.kotlin.filmfocus.model.room.TimelineItemDatabase
import amichealpalmer.kotlin.filmfocus.model.room.TimelineItemRepository
import amichealpalmer.kotlin.filmfocus.model.room.WatchlistItemDatabase
import amichealpalmer.kotlin.filmfocus.model.room.WatchlistItemRepository
import amichealpalmer.kotlin.filmfocus.viewmodel.BrowseViewModelFactory
import amichealpalmer.kotlin.filmfocus.viewmodel.TimelineViewModelFactory
import amichealpalmer.kotlin.filmfocus.viewmodel.WatchlistViewModelFactory
import android.content.Context
import androidx.fragment.app.Fragment

object InjectorUtils {

    private fun getFilmThumbnailRepository(context: Context): FilmThumbnailRepository {
        return FilmThumbnailRepository.getInstance(context.applicationContext)
    }

    private fun getWatchlistItemRepository(context: Context): WatchlistItemRepository {
        return WatchlistItemRepository.getInstance(WatchlistItemDatabase.getInstance(context.applicationContext).watchlistItemDao())
    }

    private fun getTimelineItemRepository(context: Context): TimelineItemRepository {
        return TimelineItemRepository.getInstance(TimelineItemDatabase.getInstance(context.applicationContext).timelineItemDao())
    }

    fun provideBrowseViewModelFactory(context: Context): BrowseViewModelFactory {
        val repository = getFilmThumbnailRepository(context)
        val watchlistRepository = getWatchlistItemRepository(context)
        val timelineRepository = getTimelineItemRepository(context)
        return BrowseViewModelFactory(repository, watchlistRepository, timelineRepository)
    }

    fun provideWatchlistViewModelFactory(fragment: Fragment): WatchlistViewModelFactory {
        val repository = getWatchlistItemRepository(fragment.requireContext())
        val timelineRepository = getTimelineItemRepository(fragment.requireContext())
        return WatchlistViewModelFactory(repository, timelineRepository, fragment)
    }

    fun provideTimelineViewModelFactory(fragment: Fragment): TimelineViewModelFactory {
        val repository = getTimelineItemRepository(fragment.requireContext())
        val watchlistRepository = getWatchlistItemRepository(fragment.requireContext())
        return TimelineViewModelFactory(repository, watchlistRepository, fragment)
    }


}