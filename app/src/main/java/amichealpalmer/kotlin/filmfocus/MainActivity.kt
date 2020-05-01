package amichealpalmer.kotlin.filmfocus


import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.model.TimelineItem
import amichealpalmer.kotlin.filmfocus.utilities.sharedprefs.TimelineItemsSharedPrefUtil
import amichealpalmer.kotlin.filmfocus.utilities.sharedprefs.WatchlistSharedPrefUtil
import amichealpalmer.kotlin.filmfocus.view.BrowseFragment
import amichealpalmer.kotlin.filmfocus.view.HistoryFragment
import amichealpalmer.kotlin.filmfocus.view.WatchlistFragment
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.view.*

class MainActivity : AppCompatActivity(), WatchlistFragment.WatchlistFragmentDataListener, BrowseFragment.onResultActionListener, HistoryFragment.OnTimelineItemSelectedListener {

    private val TAG = "MainActivity"

    //private lateinit var toolbar: Toolbar

    private lateinit var timelineSharedPrefUtil: TimelineItemsSharedPrefUtil
    private lateinit var watchlistSharedPrefUtil: WatchlistSharedPrefUtil

    private var appBarConfiguration: AppBarConfiguration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // Must initialize these fields before super call so they are available for fragments
        timelineSharedPrefUtil = TimelineItemsSharedPrefUtil(this)
        watchlistSharedPrefUtil = WatchlistSharedPrefUtil(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(drawer_layout.toolbar)
        var navController = findNavController(R.id.activity_nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_browse_fragment, R.id.nav_watchlist_fragment, R.id.nav_history_fragment
        ), drawer_layout)

        nav_view.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration!!)

        // toolbar = findViewById(R.id.toolbar)
        Log.d(TAG, ".onCreate finished")
    }


    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.activity_nav_host_fragment).navigateUp(appBarConfiguration!!) || super.onSupportNavigateUp()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        Log.d(TAG, ".onRestoreInstanceState called")
        super.onRestoreInstanceState(savedInstanceState)
    }

    fun closeKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


    override fun onAttachFragment(fragment: Fragment) {
        if (fragment is WatchlistFragment) {
            fragment.setWatchlistFragmentDataListener(this)
        }
        if (fragment is BrowseFragment) {
            fragment.setOnResultActionListener(this)
        }
        if (fragment is HistoryFragment) {
            fragment.setOnTimelineItemSelectedListener(this)
        }
    }

    // todo: move ALL of these to a helper object where the sharedPrefutil objects are safely checked - if the screen is rotated, this activity is destroyed meaning the util objects are too. helper object should implement all of the interfaces
    override fun retrieveWatchlist(): ArrayList<FilmThumbnail> {
        return watchlistSharedPrefUtil.loadWatchlist()
    }

    override fun clearWatchlist() {
        watchlistSharedPrefUtil.clearWatchlist()
    }

    override fun removeFilmFromWatchlist(film: FilmThumbnail) {
        watchlistSharedPrefUtil.removeFilmFromWatchlist(film)
    }

    override fun addItemToTimeline(timelineItem: TimelineItem) {
        watchlistSharedPrefUtil.removeFilmFromWatchlist(timelineItem.film)
        timelineSharedPrefUtil.addItemToTimeline(timelineItem)
    }

    // Return boolean back to fragment so we can display correct toast message
    override fun addFilmToWatchlistFromHistory(film: FilmThumbnail): Boolean {
        return watchlistSharedPrefUtil.addFilmToWatchlist(film)
    }

    override fun clearHistory(): Boolean {
        return timelineSharedPrefUtil.clearTimeline()
    }

    override fun removeItemFromHistory(timelineItem: TimelineItem) {
        timelineSharedPrefUtil.removeItemFromTimeline(timelineItem)
    }

    override fun updateHistoryItem(timelineItem: TimelineItem) {
        timelineSharedPrefUtil.updateTimelineItem(timelineItem)
    }

    override fun retrieveHistory(): ArrayList<TimelineItem> {
        return timelineSharedPrefUtil.loadTimelineItems()
    }

    // Returns false back to BrowseFragment if already present in Watchlist
    override fun addFilmToWatchlistFromBrowse(filmThumbnail: FilmThumbnail): Boolean {
        return watchlistSharedPrefUtil.addFilmToWatchlist(filmThumbnail)
    }

    override fun markFilmAsWatchedFromBrowse(timelineItem: TimelineItem) {
        timelineSharedPrefUtil.addItemToTimeline(timelineItem)
    }


}


