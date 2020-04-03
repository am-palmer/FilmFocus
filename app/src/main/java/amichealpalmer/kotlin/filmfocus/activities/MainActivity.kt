package amichealpalmer.kotlin.filmfocus.activities

//import amichealpalmer.kotlin.filmfocus.fragments.ACTION_TYPE
import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.data.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.data.TimelineItem
import amichealpalmer.kotlin.filmfocus.fragments.*
import amichealpalmer.kotlin.filmfocus.helpers.LocalDateSerializer
import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.view.*
import org.joda.time.LocalDate
import java.lang.reflect.Type


// todo: see trello
// todo next: implement the dialog box which shows when user marks film as watched (in watchlist view and in the browse view)
class MainActivity : AppCompatActivity(), WatchlistFragment.OnWatchlistActionListener, BrowseFragment.onResultActionListener, HistoryFragment.OnTimelineItemSelectedListener { // todo: disperse as much logic into the fragments as possible

    internal val OMDB_SEARCH_QUERY = "OMDB_SEACH_QUERY"
    internal val FILM_DETAILS_TRANSFER = "FILM_DETAILS_TRANSFER"
    internal val SHAREDPREFS_KEY_WATCHLIST = "watchlist"
    internal val SHAREDPREFS_KEY_TIMELINE = "timelineList"

    val TAG = "MainActivity"

    private lateinit var watchlist: ArrayList<FilmThumbnail> // The user's Watchlist, stored in SharedPrefs
    private lateinit var timelineList: ArrayList<TimelineItem> // List of items in the user's history


    private var currentFragment: Fragment? = null // search or watchlist
    private lateinit var mDrawer: DrawerLayout
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private val SHARED_PREFS = "sharedPrefs"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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

        Log.d(TAG, "Set content view / navigation drawer done")

        fab.setOnClickListener { view ->
            // todo action button
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        // Load search by default
        val fragment = BrowseFragment.newInstance(null)
        setTitle("Browse")
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.main_frame_layout_fragment_holder, fragment).commit()
        currentFragment = fragment

        loadData()
        //clearData()
        Log.d(TAG, ".onCreate finished")

    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { // Handles action bar item taps
        if (drawerToggle.onOptionsItemSelected(item)) return true

        return when (item.itemId) {
            //R.id.action_settings -> true
            android.R.id.home -> {
                Log.d(TAG, ".onOptionsItemSelected: drawer button tapped")
                mDrawer.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig)
    }

    @SuppressLint("RestrictedApi")
    internal fun activateToolbar(enableHome: Boolean) {
        Log.d(TAG, ".activateToolbar")

        var toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDefaultDisplayHomeAsUpEnabled(enableHome)
    }

    private fun setupDrawerContent(navigationView: NavigationView) {
        Log.d(TAG, ".setupDrawerContent: setting nav view itemselectedlistener")
        navigationView.setNavigationItemSelectedListener { menuItem ->
            selectDrawerItem(menuItem)
            true
        }
    }

    fun selectDrawerItem(menuItem: MenuItem) { // Create a new fragment and specify the fragment to show based on nav item clicked
        var fragment: Fragment? = null
        val fragmentClass: Class<*>
        fragmentClass = when (menuItem.itemId) {
            R.id.nav_first_fragment -> BrowseFragment::class.java
            R.id.nav_second_fragment -> WatchlistFragment::class.java
            R.id.nav_third_fragment -> HistoryFragment::class.java
            else -> BrowseFragment::class.java
        }

        // todo: move these to a specific function, keep them out of the UI
        // todo: replace if with when
        // todo: don't reconstruct the fragments every time?
        if (currentFragment!!::class.java == fragmentClass) {
            Log.d(TAG, ".selectDrawerItem: user clicked fragment we're currently in")
            // User clicked the fragment button we're in; do nothing
        } else {
            if (fragmentClass == BrowseFragment::class.java) { // todo: preserve state if user has already made a search
                fragment = fragmentClass.newInstance()
                currentFragment = fragment
            } else if (fragmentClass == WatchlistFragment::class.java) {
                fragment = fragmentClass.newInstance()
                val bundle = Bundle()
                bundle.putParcelableArrayList("watchlist", watchlist)
                fragment.arguments = bundle
                currentFragment = fragment
            } else if (fragmentClass == HistoryFragment::class.java) {
                fragment = fragmentClass.newInstance()
                val bundle = Bundle()
                bundle.putParcelableArrayList("timelineList", timelineList)
                fragment.arguments = bundle
                currentFragment = fragment
            }

            // Insert the fragment by replacing any existing fragment
            val fragmentManager = supportFragmentManager

            fragmentManager.beginTransaction().replace(R.id.main_frame_layout_fragment_holder, fragment!!).commit()

            menuItem.isChecked = true
            title = menuItem.title
        }
        mDrawer.closeDrawers()
    }

    fun setupDrawerToggle(): ActionBarDrawerToggle {
        return ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close)
    }

    override fun onNewIntent(intent: Intent?) { // todo: move this logic into the browse fragment, then delete
        super.onNewIntent(intent)
        Log.d(TAG, ".onNewIntent called")
        if (Intent.ACTION_SEARCH == intent!!.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            Log.d(TAG, ".handleIntent: received new searchHelper query: $query")
            // searchHelper().searchByTitleKeyword(query!!)

            // Building the search fragment
            val fragment = BrowseFragment()
            var args = Bundle()
            args.putString("searchString", query)
            fragment.arguments = args
            var transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.main_frame_layout_fragment_holder, fragment)
            transaction.commit()
        } else {
            Log.d(TAG, "intent.action != Intent.ACTION_SEARCH")
        }
    }

    fun saveData() {
        Log.d(TAG, ".saveData called, saving data to Shared Preferences")
        val sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // We use GSON to do custom objects (specifically, an ArrayList of FilmThumbnails, and an ArrayList of TimelineItems)
        //val gson = Gson()
        val builder: GsonBuilder = GsonBuilder()
        builder.registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
        // Watchlist
        val gson = builder.create()
        var watchlistJson = gson.toJson(watchlist)
        editor.putString(SHAREDPREFS_KEY_WATCHLIST, watchlistJson)

        // Timeline items
        var timelineJson = gson.toJson(timelineList)
        editor.putString(SHAREDPREFS_KEY_TIMELINE, timelineJson)

        editor.apply()
    }

    private fun clearData() { // For testing
        Log.d(TAG, ".clearData called, clearing shared preferences")
        val sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
    }

    fun loadData() {
        Log.d(TAG, ".loadData called, loading data from Shared Preferences")
        val sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val builder: GsonBuilder = GsonBuilder()
        builder.registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
        // Watchlist
        val gson = builder.create()

        // Load the watchlist
        val watchlistJson = sharedPreferences.getString(SHAREDPREFS_KEY_WATCHLIST, null)
        val watchlistType: Type = object : TypeToken<ArrayList<FilmThumbnail>>() {}.type
        if (watchlistJson == null) { // Build a new watchlist
            Log.d(TAG, ".loadData: watchlist could not be loaded / doesn't exist yet. Making a new watchlist")
            watchlist = ArrayList<FilmThumbnail>()
        } else {
            val watchlistRetrieved = gson.fromJson(watchlistJson, watchlistType) as ArrayList<FilmThumbnail>?
            Log.d(TAG, ".loadData: watchlist loaded. it contains ${watchlistRetrieved!!.size} items")
            watchlist = watchlistRetrieved
        }

        // Load the timeline items
        val timelineJson = sharedPreferences.getString(SHAREDPREFS_KEY_TIMELINE, null)
        val timelineType: Type = object : TypeToken<ArrayList<TimelineItem>>() {}.type
        if (timelineJson == null) {
            Log.d(TAG, ".loadData: timeline could not be loaded / does not exist yet. Making a new watchlist")
            timelineList = ArrayList<TimelineItem>()
        } else {
            val timelineRetrieved = gson.fromJson(timelineJson, timelineType) as ArrayList<TimelineItem>
            Log.d(TAG, ".loadData: timeline retrieved")
            timelineList = timelineRetrieved
            //timelineList = ArrayList<TimelineItem>()
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

    override fun onFilmSelected(bundle: Bundle, typeWATCHLIST: WATCHLIST_FILM_CONTEXT_ACTION_TYPE) { // todo: merge this listener with the below into an 'on context item selected' listener
        Log.d(TAG, ".onFilmSelected is called with TYPE: ${typeWATCHLIST.name}")
        if (typeWATCHLIST == WATCHLIST_FILM_CONTEXT_ACTION_TYPE.WATCHLIST_REMOVE) {
            val film = bundle.getParcelable<FilmThumbnail>("film")
            if (film == null) {
                Log.e(TAG, ".onFilmSelected: film null in bundle") // error handling?
            } else {
                watchlist.remove(film)
                // Update local data
                // Todo: make sure this is not a costly operation, if it is must save changes via some other method
                saveData()
            }
        } else if (typeWATCHLIST == WATCHLIST_FILM_CONTEXT_ACTION_TYPE.WATCHLIST_MARK_WATCHED) {
            val timelineItem = bundle.getParcelable<TimelineItem>("timelineItem")
            if (timelineItem == null) {
                Log.e(TAG, ".onFilmSelected: timelineItem null in bundle") // error handling?
            } else {
                timelineList.add(timelineItem)
                watchlist.remove(timelineItem.film)
                Toast.makeText(this, "Marked ${timelineItem.film.title} as watched", Toast.LENGTH_SHORT).show()
                saveData()
            }
        }
    }

    override fun onWatchlistMenuItemSelected(bundle: Bundle, actionType: WATCHLIST_MENU_ITEM_ACTION_TYPE) {
        Log.d(TAG, "watchlist is size ${watchlist.size}") // todo: why is it size zero?????
        when (actionType) {
            WATCHLIST_MENU_ITEM_ACTION_TYPE.REMOVE_ALL -> {
                if (watchlist.isEmpty()) {
                    Toast.makeText(this, "The Watchlist is already empty", Toast.LENGTH_SHORT).show()
                } else {
                    watchlist.clear()
                    Toast.makeText(this, "Cleared Watchlist", Toast.LENGTH_SHORT).show()
                    saveData()
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
        if (type == BROWSE_FILM_CONTEXT_ACTION_TYPE.ADD_TO_WATCHLIST) {
            val film = bundle.getParcelable<FilmThumbnail>("film")
            if (film != null) {
                helperAddToWatchlist(film)
            } else {
                Log.e(TAG, "onSearchResultAction: film from bundle is null.")
            }
        } else if (type == BROWSE_FILM_CONTEXT_ACTION_TYPE.MARK_WATCHED) {
            // todo: code duplication with above - merge these listeners?
            val timelineItem = bundle.getParcelable<TimelineItem>("timelineItem")
            if (timelineItem == null) {
                Log.e(TAG, ".onFilmSelected: timelineItem null in bundle") // error handling?
            } else {
                timelineList.add(timelineItem)
                watchlist.remove(timelineItem.film)
                Toast.makeText(this, "Marked ${timelineItem.film.title} as watched", Toast.LENGTH_SHORT).show()
                saveData()
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
}

class ExtraTools {

//    fun createTestWatchlist(): ArrayList<FilmThumbnail> {
//        var resultList = ArrayList<FilmThumbnail>()
//        // Add some 'results' to the list
//        resultList.add(FilmThumbnail("Blade Runner", "", "tt0083658", "", "https://upload.wikimedia.org/wikipedia/en/thumb/9/9f/Blade_Runner_(1982_poster).png/220px-Blade_Runner_(1982_poster).png"))
//        resultList.add(FilmThumbnail("Predator", "", "tt0093773", "", "https://upload.wikimedia.org/wikipedia/en/9/95/Predator_Movie.jpg"))
//        resultList.add(FilmThumbnail("The Thing", "", "tt0084787", "", "https://upload.wikimedia.org/wikipedia/en/a/a6/The_Thing_(1982)_theatrical_poster.jpg"))
//        resultList.add(FilmThumbnail("The Fly", "", "tt0091064", "", "https://upload.wikimedia.org/wikipedia/en/a/aa/Fly_poster.jpg"))
//        return resultList
//    }


}



