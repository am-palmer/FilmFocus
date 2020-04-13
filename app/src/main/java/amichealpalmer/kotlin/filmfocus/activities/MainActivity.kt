package amichealpalmer.kotlin.filmfocus.activities


import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.data.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.data.TimelineItem
import amichealpalmer.kotlin.filmfocus.fragments.*
import amichealpalmer.kotlin.filmfocus.helpers.LocalDateSerializer
import android.app.SearchManager
import android.content.Context
import android.content.Intent
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

class MainActivity : AppCompatActivity(), WatchlistFragment.OnWatchlistActionListener, BrowseFragment.onResultActionListener, HistoryFragment.OnTimelineItemSelectedListener { // todo: disperse as much logic into the fragments as possible

    private val SHAREDPREFS_KEY_WATCHLIST = "watchlist"
    private val SHAREDPREFS_KEY_TIMELINE = "timelineList"

    private val TAG = "MainActivity"

    private lateinit var watchlist: ArrayList<FilmThumbnail> // The user's Watchlist, stored in SharedPrefs
    private lateinit var timelineList: ArrayList<TimelineItem> // List of items in the user's history

    private var fragmentID: FRAGMENT_ID? = null
    private lateinit var mDrawer: DrawerLayout
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var toolbar: Toolbar
    private val SHARED_PREFS = "sharedPrefs"

    private var browseFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) { // First-time load, show a new browseFragment
            val fragment = BrowseFragment.newInstance(null)
            browseFragment = fragment
            title = "Browse"
            val fragmentManager = supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.main_frame_layout_fragment_holder, fragment, FRAGMENT_ID.BROWSE.name).commit()
            fragmentID = FRAGMENT_ID.BROWSE
            loadData()
        } else { // Restore data from saved instance state
            try {
                watchlist = savedInstanceState.getParcelableArrayList("watchlist")!!
                timelineList = savedInstanceState.getParcelableArrayList("timelineList")!!
                fragmentID = FRAGMENT_ID.valueOf(savedInstanceState.getString("currentFragment")!!) // Use this to figure out which fragment should be selected?

                // May not be necessary
                when (fragmentID) {
                    FRAGMENT_ID.BROWSE -> title = "Browse"
                    FRAGMENT_ID.HISTORY -> title = "History"
                    FRAGMENT_ID.WATCHLIST -> title = "Watchlist"
                }

            } catch (e: NullPointerException) {
                Log.wtf(TAG, ".onCreate: failed to load member variables from saved instance state")
                Log.wtf(TAG, e.stackTrace.toString())
            }

        }

        setSupportActionBar(drawer_layout.toolbar)
        toolbar = findViewById(R.id.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        mDrawer = findViewById(R.id.drawer_layout)
        drawerToggle = setupDrawerToggle()
        drawerToggle.isDrawerIndicatorEnabled = true
        drawerToggle.syncState()
        val nvDrawer = findViewById<NavigationView>(R.id.nvView)
        setupDrawerContent(nvDrawer)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        Log.d(TAG, "watchlist check: size is ${watchlist.size}")
        Log.d(TAG, ".onCreate finished")

    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList("watchlist", watchlist)
        outState.putParcelableArrayList("timelineList", timelineList)
        outState.putString("currentFragment", fragmentID!!.name)

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        Log.d(TAG, ".onConfiguration changed: starts")
        super.onConfigurationChanged(newConfig)
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        return super.onCreateOptionsMenu(menu)
    }

    private fun setupDrawerContent(navigationView: NavigationView) {
        Log.d(TAG, ".setupDrawerContent: setting nav view itemselectedlistener")
        navigationView.setNavigationItemSelectedListener { menuItem ->
            selectDrawerItem(menuItem)
            closeKeyboard()
            true
        }
    }

    private fun selectDrawerItem(menuItem: MenuItem) {
        var fragment: Fragment? = null
        val fragmentIDClass: FRAGMENT_ID?
        val fragmentManager = supportFragmentManager
        Log.d("TAG", "menuItem itemId is: ${menuItem.itemId}")
        fragmentIDClass = when (menuItem.itemId) {
            R.id.nav_first_fragment -> FRAGMENT_ID.BROWSE
            R.id.nav_second_fragment -> FRAGMENT_ID.WATCHLIST
            R.id.nav_third_fragment -> FRAGMENT_ID.HISTORY
            else -> FRAGMENT_ID.BROWSE
        }
        Log.d(TAG, "fragmentIDClass is: ${fragmentIDClass.name} ")
        Log.d(TAG, "fragmentID is: ${fragmentID!!.name}")
        if (fragmentIDClass == fragmentID) {
            Log.d(TAG, "user clicked fragment we're already in (fragmentIDClass == fragmentID)")
            // Do nothing
        } else {
            when (fragmentIDClass) {
                FRAGMENT_ID.BROWSE -> {
                    fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_ID.BROWSE.name)
                            ?: BrowseFragment.newInstance(null)
                    title = "Browse"
                    fragmentID = FRAGMENT_ID.BROWSE
                }
                FRAGMENT_ID.WATCHLIST -> {
                    fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_ID.WATCHLIST.name)
                            ?: WatchlistFragment.newInstance(watchlist)
                    title = "Watchlist"
                    fragmentID = FRAGMENT_ID.WATCHLIST
                }
                FRAGMENT_ID.HISTORY -> {
                    fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_ID.HISTORY.name)
                            ?: HistoryFragment.newInstance(timelineList)
                    val historyFragment = fragment as HistoryFragment
                    historyFragment.forceTimelineRefresh(timelineList) // Ensuring the view is updated correctly
                    title = "History"
                    fragmentID = FRAGMENT_ID.HISTORY
                }
            }

            // Insert the fragment by replacing any existing fragment
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.main_frame_layout_fragment_holder, fragment, fragmentID!!.name)
            transaction.addToBackStack(null)
            transaction.commit()
            fragmentManager.executePendingTransactions()
            menuItem.isChecked = true
            title = menuItem.title
        }
        mDrawer.closeDrawers()
    }

    private fun setupDrawerToggle(): ActionBarDrawerToggle {
        return object : ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close) {
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d(TAG, ".onNewIntent called")
        if (Intent.ACTION_SEARCH == intent!!.action) {
            closeKeyboard()
            try {
                val browse = browseFragment as BrowseFragment
                browse.searchHelper().searchByTitleKeyword(intent.getStringExtra(SearchManager.QUERY)!!)
            } catch (e: java.lang.NullPointerException) {
                Log.wtf(TAG, ".onNewIntent Action Search: but browseFragment is null?")
            }
        } else {
            Log.d(TAG, "intent.action != Intent.ACTION_SEARCH")
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

    @Suppress("unused")
    private fun clearData() { // Test purposes
        Log.d(TAG, ".clearData called, clearing shared preferences")
        val sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
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

    override fun onFilmSelected(bundle: Bundle, typeWATCHLIST: WATCHLIST_FILM_CONTEXT_ACTION_TYPE) { // todo: merge this listener with the below into an 'on context item selected' listener, and use bundle
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


