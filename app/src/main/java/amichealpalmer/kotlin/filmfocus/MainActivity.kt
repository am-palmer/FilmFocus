package amichealpalmer.kotlin.filmfocus


import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.model.TimelineItem
import amichealpalmer.kotlin.filmfocus.utilities.sharedprefs.TimelineItemsSharedPrefUtil
import amichealpalmer.kotlin.filmfocus.utilities.sharedprefs.WatchlistSharedPrefUtil
import amichealpalmer.kotlin.filmfocus.view.BrowseFragment
import amichealpalmer.kotlin.filmfocus.view.HistoryFragment
import amichealpalmer.kotlin.filmfocus.view.WatchlistFragment
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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

    private lateinit var toolbar: Toolbar

    private lateinit var timelineSharedPrefUtil: TimelineItemsSharedPrefUtil
    private lateinit var watchlistSharedPrefUtil: WatchlistSharedPrefUtil

    private var appBarConfiguration: AppBarConfiguration? = null

    // todo: implement nav component for nav drawer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(drawer_layout.toolbar)
        timelineSharedPrefUtil = TimelineItemsSharedPrefUtil(this)
        watchlistSharedPrefUtil = WatchlistSharedPrefUtil(this)
        var navController = findNavController(R.id.activity_nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_browse_fragment, R.id.nav_watchlist_fragment, R.id.nav_history_fragment
        ), drawer_layout)

        nav_view.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration!!)

//        if (savedInstanceState == null) { // First-time load, show a new browseFragment
//            // todo: we may need to remove this if nav graph is handling it
//            val fragment = BrowseFragment.newInstance(null)
//            browseFragment = fragment
//            title = "Browse"
//
//            val fragmentManager = supportFragmentManager
//            fragmentManager.beginTransaction().replace(R.id.activity_nav_host_fragment, fragment, FRAGMENT_ID.BROWSE.name).commit()
//            fragmentID = FRAGMENT_ID.BROWSE
//        } else { // Restore data from saved instance state
//            try {
//                //  watchlist = savedInstanceState.getParcelableArrayList("watchlist")!!
//                //  timelineList = savedInstanceState.getParcelableArrayList("timelineList")!!
//                //fragmentID = FRAGMENT_ID.valueOf(savedInstanceState.getString("currentFragment")!!) // Use this to figure out which fragment should be selected?
////
////                // May not be necessary
////                when (fragmentID) {
////                    FRAGMENT_ID.BROWSE -> title = "Browse"
////                    FRAGMENT_ID.HISTORY -> title = "History"
////                    FRAGMENT_ID.WATCHLIST -> title = "Watchlist"
////                }
//
//            } catch (e: NullPointerException) {
//                Log.wtf(TAG, ".onCreate: failed to load member variables from saved instance state")
//                Log.wtf(TAG, e.stackTrace.toString())
//            }
//
//        }
        toolbar = findViewById(R.id.toolbar)
        //supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        //supportActionBar!!.setDisplayShowTitleEnabled(false)
        // mDrawer = findViewById(R.id.drawer_layout)
        //drawerToggle = setupDrawerToggle()
        //drawerToggle.isDrawerIndicatorEnabled = true
        // drawerToggle.syncState()
        // val nvDrawer = findViewById<NavigationView>(R.id.nav_view)
        //setupDrawerContent(nvDrawer)
        // supportActionBar!!.setDisplayShowTitleEnabled(true)
        // Log.d(TAG, "watchlist check: size is ${watchlist.size}")
        Log.d(TAG, ".onCreate finished")

    }


    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.activity_nav_host_fragment).navigateUp(appBarConfiguration!!) || super.onSupportNavigateUp()
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        Log.d(TAG, ".onConfiguration changed: starts")
        super.onConfigurationChanged(newConfig)
        // Pass any configuration change to the drawer toggles
        // drawerToggle.onConfigurationChanged(newConfig)
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

    override fun addFilmToWatchlistFromHistory(film: FilmThumbnail): Boolean {
        return watchlistSharedPrefUtil.addFilmToWatchlist(film) // Return boolean back to fragment so we can display correct toast message
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

//    override fun onSearchResultAction(bundle: Bundle, type: BROWSE_FILM_CONTEXT_ACTION_TYPE) {
//        when (type) {
//            BROWSE_FILM_CONTEXT_ACTION_TYPE.MARK_WATCHED -> {
//                try {
//                    val timelineItem = bundle.getParcelable<TimelineItem>("timelineItem")
//                    //timelineList.add(timelineItem!!)
//                    //watchlist.remove(timelineItem.film)
//                    timelineSharedPrefUtil.addItemToTimeline(timelineItem!!)
//                    watchlistSharedPrefUtil.removeFilmFromWatchlist(timelineItem.film)
//                    Toast.makeText(this, "Marked ${timelineItem.film.title} as watched", Toast.LENGTH_SHORT).show()
//                    //saveData()
//                } catch (e: NullPointerException) {
//                    Log.wtf(TAG, ".onFilmSelected: timelineItem null in bundle")
//                }
//            }
//            BROWSE_FILM_CONTEXT_ACTION_TYPE.ADD_TO_WATCHLIST -> {
//                try {
//                    val film = bundle.getParcelable<FilmThumbnail>("film")
//                    //helperAddToWatchlist(film!!)
//                    val result = watchlistSharedPrefUtil.addFilmToWatchlist(film!!)
//                    when (result) {
//                        true -> Toast.makeText(this, "Added ${film.title} to Watchlist", Toast.LENGTH_SHORT).show()
//                        false -> Toast.makeText(this, "${film.title} is already on Watchlist", Toast.LENGTH_SHORT).show()
//                    }
//                    Log.e(TAG, "onSearchResultAction: film from bundle is null.")
//                } catch (e: NullPointerException) {
//                    Log.wtf(TAG, ".onSearchResultAction: film in bundle is null.")
//                }
//            }
//        }
//}

}


