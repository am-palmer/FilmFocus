package amichealpalmer.kotlin.filmfocus


import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.model.TimelineItem
import amichealpalmer.kotlin.filmfocus.utilities.LocalDateSerializer
import amichealpalmer.kotlin.filmfocus.view.*
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.view.*
import org.joda.time.LocalDate
import java.lang.reflect.Type

enum class FRAGMENT_ID {
    BROWSE, WATCHLIST, HISTORY
}

class MainActivity : AppCompatActivity(), WatchlistFragment.OnWatchlistActionListener, BrowseFragment.onResultActionListener, HistoryFragment.OnTimelineItemSelectedListener {

    //private val SHAREDPREFS_KEY_WATCHLIST = "watchlist"
    //private val SHAREDPREFS_KEY_TIMELINE = "timelineList"

    private val TAG = "MainActivity"

    //private lateinit var watchlist: ArrayList<FilmThumbnail> // The user's Watchlist, stored in SharedPrefs
    //private lateinit var timelineList: ArrayList<TimelineItem> // List of items in the user's history

    private var fragmentID: FRAGMENT_ID? = null
    private lateinit var mDrawer: DrawerLayout
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var toolbar: Toolbar
    private val SHARED_PREFS = "sharedPrefs"

    private var appBarConfiguration: AppBarConfiguration? = null

    private var browseFragment: Fragment? = null

    // todo: implement nav component for nav drawer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(drawer_layout.toolbar)

        var navController = findNavController(R.id.activity_nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_browse_fragment, R.id.nav_watchlist_fragment, R.id.nav_history_fragment
        ), drawer_layout)

        nav_view.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration!!)

        if (savedInstanceState == null) { // First-time load, show a new browseFragment
            val fragment = BrowseFragment.newInstance(null)
            browseFragment = fragment
            title = "Browse"
            val fragmentManager = supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.activity_nav_host_fragment, fragment, FRAGMENT_ID.BROWSE.name).commit()
            fragmentID = FRAGMENT_ID.BROWSE
            loadData()
        } else { // Restore data from saved instance state
            try {
                watchlist = savedInstanceState.getParcelableArrayList("watchlist")!!
                timelineList = savedInstanceState.getParcelableArrayList("timelineList")!!
                //fragmentID = FRAGMENT_ID.valueOf(savedInstanceState.getString("currentFragment")!!) // Use this to figure out which fragment should be selected?
//
//                // May not be necessary
//                when (fragmentID) {
//                    FRAGMENT_ID.BROWSE -> title = "Browse"
//                    FRAGMENT_ID.HISTORY -> title = "History"
//                    FRAGMENT_ID.WATCHLIST -> title = "Watchlist"
//                }

            } catch (e: NullPointerException) {
                Log.wtf(TAG, ".onCreate: failed to load member variables from saved instance state")
                Log.wtf(TAG, e.stackTrace.toString())
            }

        }
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

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // Sync the toggle state after onRestoreInstanceState has occurred.
       // drawerToggle.syncState()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList("watchlist", watchlist)
        outState.putParcelableArrayList("timelineList", timelineList)
       // outState.putString("currentFragment", fragmentID!!.name)

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        Log.d(TAG, ".onConfiguration changed: starts")
        super.onConfigurationChanged(newConfig)
        // Pass any configuration change to the drawer toggles
       // drawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
       // supportActionBar?.setDisplayHomeAsUpEnabled(true)
        return super.onCreateOptionsMenu(menu)
    }

//    private fun setupDrawerContent(navigationView: NavigationView) {
//        Log.d(TAG, ".setupDrawerContent: setting nav view itemselectedlistener")
//        navigationView.setNavigationItemSelectedListener { menuItem ->
//            selectDrawerItem(menuItem)
//            closeKeyboard()
//            true
//        }
//    }

//    private fun selectDrawerItem(menuItem: MenuItem) {
//        var fragment: Fragment? = null
//        val fragmentIDClass: FRAGMENT_ID?
//        val fragmentManager = supportFragmentManager
//        Log.d("TAG", "menuItem itemId is: ${menuItem.itemId}")
//        fragmentIDClass = when (menuItem.itemId) {
//            R.id.nav_browse_fragment -> FRAGMENT_ID.BROWSE
//            R.id.nav_watchlist_fragment -> FRAGMENT_ID.WATCHLIST
//            R.id.nav_history_fragment -> FRAGMENT_ID.HISTORY
//            else -> FRAGMENT_ID.BROWSE
//        }
//        Log.d(TAG, "fragmentIDClass is: ${fragmentIDClass.name} ")
//        Log.d(TAG, "fragmentID is: ${fragmentID!!.name}")
//        if (fragmentIDClass == fragmentID) {
//            Log.d(TAG, "user clicked fragment we're already in (fragmentIDClass == fragmentID)")
//            // Do nothing
//        } else {
//            when (fragmentIDClass) {
//                FRAGMENT_ID.BROWSE -> {
//                    fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_ID.BROWSE.name)
//                            ?: BrowseFragment.newInstance(null)
//                    title = "Browse"
//                    fragmentID = FRAGMENT_ID.BROWSE
//                }
//                FRAGMENT_ID.WATCHLIST -> {
//                    fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_ID.WATCHLIST.name)
//                            ?: WatchlistFragment.newInstance(watchlist)
//                    title = "Watchlist"
//                    fragmentID = FRAGMENT_ID.WATCHLIST
//                }
//                FRAGMENT_ID.HISTORY -> {
//                    fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_ID.HISTORY.name)
//                            ?: HistoryFragment.newInstance(timelineList)
//                    val historyFragment = fragment as HistoryFragment
//                    historyFragment.forceTimelineRefresh(timelineList) // Ensuring the view is updated correctly
//                    title = "History"
//                    fragmentID = FRAGMENT_ID.HISTORY
//                }
//            }
//
//            // Insert the fragment by replacing any existing fragment
//            val transaction = fragmentManager.beginTransaction()
//            transaction.replace(R.id.activity_nav_host_fragment, fragment, fragmentID!!.name)
//            transaction.addToBackStack(null)
//            transaction.commit()
//            fragmentManager.executePendingTransactions()
//            menuItem.isChecked = true
//            title = menuItem.title
//        }
//        mDrawer.closeDrawers()
//    }

    private fun setupDrawerToggle(): ActionBarDrawerToggle {
        return object : ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close) {
        }
    }

    private fun saveData() {
        Log.d(TAG, ".saveData called, saving data to Shared Preferences")
        val sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // We use GSON to do custom objects (specifically, an ArrayList of FilmThumbnails, and an ArrayList of TimelineItems)
        val builder = GsonBuilder()
        builder.registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())

        // Watchlist
        val gson = builder.create()
        val watchlistJson = gson.toJson(watchlist)
        editor.putString(SHAREDPREFS_KEY_WATCHLIST, watchlistJson)

        // Timeline items
        val timelineJson = gson.toJson(timelineList)
        editor.putString(SHAREDPREFS_KEY_TIMELINE, timelineJson)

        editor.apply()
    }

    private fun loadData() {
        Log.d(TAG, ".loadData called, loading data from Shared Preferences")
        val sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val builder = GsonBuilder()
        builder.registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
        // Watchlist
        val gson = builder.create()

        // Load the watchlist
        val watchlistJson = sharedPreferences.getString(SHAREDPREFS_KEY_WATCHLIST, null)
        val watchlistType: Type = object : TypeToken<ArrayList<FilmThumbnail>>() {}.type
        watchlist = if (watchlistJson == null) { // Build a new watchlist
            Log.d(TAG, ".loadData: watchlist could not be loaded / doesn't exist yet. Making a new watchlist")
            ArrayList()
        } else {
            val watchlistRetrieved = gson.fromJson(watchlistJson, watchlistType) as ArrayList<FilmThumbnail>?
            Log.d(TAG, ".loadData: watchlist loaded. it contains ${watchlistRetrieved!!.size} items")
            watchlistRetrieved
        }

        // Load the timeline items
        val timelineJson = sharedPreferences.getString(SHAREDPREFS_KEY_TIMELINE, null)
        val timelineType: Type = object : TypeToken<ArrayList<TimelineItem>>() {}.type
        timelineList = if (timelineJson == null) {
            Log.d(TAG, ".loadData: timeline could not be loaded / does not exist yet. Making a new watchlist")
            ArrayList()
        } else {
            val timelineRetrieved = gson.fromJson(timelineJson, timelineType) as ArrayList<TimelineItem>
            Log.d(TAG, ".loadData: timeline retrieved")
            timelineRetrieved
        }
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
            fragment.setOnFilmSelectedListener(this)
        }
        if (fragment is BrowseFragment) {
            fragment.setOnResultActionListener(this)
        }
        if (fragment is HistoryFragment) {
            fragment.setOnTimelineItemSelectedListener(this)
        }
    }

    override fun onFilmSelected(bundle: Bundle, typeWATCHLIST: WATCHLIST_FILM_CONTEXT_ACTION_TYPE) {
        Log.d(TAG, ".onFilmSelected is called with TYPE: ${typeWATCHLIST.name}")
        if (typeWATCHLIST == WATCHLIST_FILM_CONTEXT_ACTION_TYPE.WATCHLIST_REMOVE) {
            val film = bundle.getParcelable<FilmThumbnail>("film")
            if (film == null) {
                Log.e(TAG, ".onFilmSelected: film null in bundle")
            } else {
                watchlist.remove(film)
                saveData()
            }
        } else if (typeWATCHLIST == WATCHLIST_FILM_CONTEXT_ACTION_TYPE.WATCHLIST_MARK_WATCHED) {
            val timelineItem = bundle.getParcelable<TimelineItem>("timelineItem")
            if (timelineItem == null) {
                Log.e(TAG, ".onFilmSelected: timelineItem null in bundle")
            } else {
                timelineList.add(timelineItem)
                watchlist.remove(timelineItem.film)
                Toast.makeText(this, "Marked ${timelineItem.film.title} as watched", Toast.LENGTH_SHORT).show()
                saveData()
            }
        }
    }

    override fun onWatchlistMenuItemSelected(bundle: Bundle, actionType: WATCHLIST_MENU_ITEM_ACTION_TYPE) {
        when (actionType) {
            WATCHLIST_MENU_ITEM_ACTION_TYPE.REMOVE_ALL -> {
                try {
                    val currentWatchlist = bundle.getParcelableArrayList<FilmThumbnail>("watchlist")
                    Log.d(TAG, ".onWatchlistMenuItemSelected: watchlist has ${currentWatchlist!!.size} items")
                    if (currentWatchlist.isEmpty()) {
                        Toast.makeText(this, "The Watchlist is already empty", Toast.LENGTH_SHORT).show()
                    } else {
                        watchlist.clear()
                        Toast.makeText(this, "Cleared Watchlist", Toast.LENGTH_SHORT).show()
                        saveData()
                    }
                } catch (e: NullPointerException) {
                    Log.wtf(TAG, ".onWatchlistMenuItemSelected")
                    Log.wtf(TAG, e.stackTrace.toString())
                }
            }
        }
    }

    private fun helperAddToWatchlist(film: FilmThumbnail) {
        var inWatchlist = false
        for (f in watchlist) {
            if (f.imdbID == film.imdbID) inWatchlist = true
        }
        if (!inWatchlist) {
            watchlist.add(film)
            Toast.makeText(this, "Added ${film.title} to Watchlist", Toast.LENGTH_SHORT).show()
            saveData()
        } else {
            Toast.makeText(this, "${film.title} is already on Watchlist", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSearchResultAction(bundle: Bundle, type: BROWSE_FILM_CONTEXT_ACTION_TYPE) {
        when (type) {
            BROWSE_FILM_CONTEXT_ACTION_TYPE.MARK_WATCHED -> {
                try {
                    val timelineItem = bundle.getParcelable<TimelineItem>("timelineItem")
                    timelineList.add(timelineItem!!)
                    watchlist.remove(timelineItem.film)
                    Toast.makeText(this, "Marked ${timelineItem.film.title} as watched", Toast.LENGTH_SHORT).show()
                    saveData()
                } catch (e: NullPointerException) {
                    Log.wtf(TAG, ".onFilmSelected: timelineItem null in bundle")
                }
            }
            BROWSE_FILM_CONTEXT_ACTION_TYPE.ADD_TO_WATCHLIST -> {
                try {
                    val film = bundle.getParcelable<FilmThumbnail>("film")
                    helperAddToWatchlist(film!!)
                    Log.e(TAG, "onSearchResultAction: film from bundle is null.")
                } catch (e: NullPointerException) {
                    Log.wtf(TAG, ".onSearchResultAction: film in bundle is null.")
                }
            }
        }
    }

    override fun onTimelineItemSelected(item: TimelineItem, type: TIMELINE_ITEM_CONTEXT_ACTION_TYPE) {
        when (type) {
            TIMELINE_ITEM_CONTEXT_ACTION_TYPE.TIMELINE_ITEM_REMOVE -> {
                timelineList.remove(item)
                Toast.makeText(this, "Removed ${item.film.title} from History", Toast.LENGTH_SHORT).show()
                saveData()
            }
            TIMELINE_ITEM_CONTEXT_ACTION_TYPE.TIMELINE_ADD_TO_WATCHLIST -> {
                helperAddToWatchlist(item.film)
            }
            TIMELINE_ITEM_CONTEXT_ACTION_TYPE.TIMELINE_ITEM_UPDATE -> {
                // Somewhat inefficient - but probably won't be called often enough for it to matter
                for (listItem in timelineList) {
                    if (listItem.date == item.date && listItem.film.imdbID == item.film.imdbID) {
                        val position = timelineList.indexOf(listItem)
                        timelineList[position] = item
                        saveData()
                    }
                }
            }
        }
    }

    override fun onHistoryMenuItemSelected(bundle: Bundle, actionType: HISTORY_MENU_ITEM_ACTION_TYPE) {
        when (actionType) {
            HISTORY_MENU_ITEM_ACTION_TYPE.REMOVE_ALL -> {
                try {
                    val currentTimelineList = bundle.getParcelableArrayList<TimelineItem>("timelineList")
                    if (currentTimelineList!!.isEmpty()) {
                        Toast.makeText(this, "The History is already empty", Toast.LENGTH_SHORT).show()
                    } else {
                        timelineList.clear()
                        Toast.makeText(this, "Cleared History", Toast.LENGTH_SHORT).show()
                        saveData()
                    }
                } catch (e: NullPointerException) {
                    Log.wtf(TAG, ".onWatchlistMenuItemSelected")
                    Log.wtf(TAG, e.stackTrace.toString())
                }
            }
        }
    }
}


