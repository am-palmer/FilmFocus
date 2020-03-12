package amichealpalmer.kotlin.filmfocus.activities

//import amichealpalmer.kotlin.filmfocus.fragments.ACTION_TYPE
import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.adapters.WatchlistRecyclerAdapter
import amichealpalmer.kotlin.filmfocus.data.Film
import amichealpalmer.kotlin.filmfocus.data.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.fragments.BrowseFragment
import amichealpalmer.kotlin.filmfocus.fragments.FILM_CONTEXT_ACTION_TYPE
import amichealpalmer.kotlin.filmfocus.fragments.WatchlistFragment
import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
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
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.view.*
import java.lang.reflect.Type


// todo: see trello

class MainActivity : AppCompatActivity(), WatchlistFragment.OnFilmSelectedListener, BrowseFragment.onResultActionListener { // todo: disperse as much logic into the fragments as possible

    internal val OMDB_SEARCH_QUERY = "OMDB_SEACH_QUERY"
    internal val FILM_DETAILS_TRANSFER = "FILM_DETAILS_TRANSFER"

    val TAG = "MainActivity"

    //val testFilm = Film("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "")
    private lateinit var watchlist: ArrayList<FilmThumbnail> // The user's Watchlist, stored in SharedPrefs
    //private var recyclerView: RecyclerView? = null


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

//        // test watchlist create
//        Log.d(TAG, ".onCreate: testing load of watchlist")
//        watchlist = createTestWatchlist()
////        watchlistHelper().inflateWatchlistFragment(watchlist)

        // testing load search fragment
        val fragment = BrowseFragment.newInstance(null)
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.main_frame_layout_fragment_holder, fragment).commit()

        //val bundle = Bundle()
        //bundle.putParcelableArrayList("watchlist", watchlist)
        //fragment.arguments = bundle
        loadData()
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
            // R.id.nav_third_fragment -> ThirdFragment::class.java
            else -> BrowseFragment::class.java
        }


        if (fragmentClass == BrowseFragment::class.java) { // todo: preserve state if user has already made a search
            fragment = fragmentClass.newInstance()
            currentFragment = fragment
        }

        // todo: retrieve the user's watchlist from somewhere local (sharedprefs)
        else if (fragmentClass == WatchlistFragment::class.java) {
            fragment = fragmentClass.newInstance()
            val bundle = Bundle()
            bundle.putParcelableArrayList("watchlist", watchlist)
            fragment.arguments = bundle
            currentFragment = fragment
        }

        // Insert the fragment by replacing any existing fragment
        val fragmentManager = supportFragmentManager

        fragmentManager.beginTransaction().replace(R.id.main_frame_layout_fragment_holder, fragment!!).commit()

        menuItem.isChecked = true
        title = menuItem.title
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

        // We use GSON to do custom objects (specifically, an ArrayList of filmThumbnails, and an ArrayList of 'watched films') todo: watched films (history) saving and retrieving
        val gson = Gson()
        var watchlistJson = gson.toJson(watchlist)
        editor.putString("watchlist", watchlistJson)
        editor.apply()
    }

    fun loadData() {
        Log.d(TAG, ".loadData called, loading data from Shared Preferences")
        val sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val gson = Gson()
        val watchlistJson = sharedPreferences.getString("watchlist", null)
        val type: Type = object : TypeToken<ArrayList<FilmThumbnail>>() {}.type

        val watchlistRetreived = gson.fromJson(watchlistJson, type) as ArrayList<FilmThumbnail>?

        if (watchlistJson == null){ // Build a new watchlist
            Log.d(TAG, ".loadData: watchlist could not be loaded / doesn't exist yet. Making a new watchlist")
            watchlist = ArrayList<FilmThumbnail>()
        } else {
            Log.d(TAG, ".loadData: watchlist loaded. it contains ${watchlistRetreived!!.size} items")
            watchlist.clear()
            watchlist.addAll(watchlistRetreived)
        }
    }

    private inner class watchlistHelper { // todo: move this logic into the fragment (if possible)
        lateinit var watchlistFragment: WatchlistFragment

        fun inflateWatchlistFragment(resultList: ArrayList<FilmThumbnail>) {
            Log.d(TAG, ".inflateWatchlistFragment starts.")
            //setContentView(R.layout.content_main)
            val fragment = WatchlistFragment()
            var args = Bundle()
            args.putParcelableArrayList("watchlist", resultList)

            Log.d(TAG, ".inflateWatchlistFragment: beginning transaction")
            fragment.arguments = args
            currentFragment = fragment
            var transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.main_frame_layout_fragment_holder, fragment) // Defined in activity_main.xml
            transaction.commit()
            Log.d(TAG, ".inflateWatchlistFragment complete")
        }


        fun removeFilmFromWatchlist(film: FilmThumbnail) { // Called by Watchlist fragment when Removing a film using ContextMenu
//            watchlist.remove(film) // todo: this change has to be stored somewhere
//            saveData() // todo: may be a costly operation to do often
//            // Recall display
//            inflateWatchlistFragment(watchlist) // todo: destroys entire ui, try to refresh instead?
        }

    }


    override fun onAttachFragment(fragment: Fragment) {
        if (fragment is WatchlistFragment) {
            fragment.setOnFilmSelectedListener(this)
        }
        if (fragment is BrowseFragment) {
            fragment.setOnResultActionListener(this)
        }
    }

    override fun onFilmSelected(film: FilmThumbnail, type: FILM_CONTEXT_ACTION_TYPE) { // todo: less idiosyncratic handling, less weird logic gates
        if (type == FILM_CONTEXT_ACTION_TYPE.WATCHLIST_REMOVE) {
            Log.d(TAG, ".onFilmSelected is called with TYPE == WATCHLIST_REMOVE")
            val watchlistFragment = currentFragment as WatchlistFragment
            val adapter = watchlistFragment.recyclerView.adapter as WatchlistRecyclerAdapter
            //watchlistHelper().removeFilmFromWatchlist(adapter.getItem(position))
            //Toast.makeText(this, "Removed", Toast.LENGTH_SHORT).show()
            watchlist.remove(film)
            // Update local data
            // Todo: make sure this is not a costly operation, if it is must save changes via some other method
            saveData()

        }
    }

    override fun onAddFilmToWatchlist(film: FilmThumbnail) {
        // todo: check if the film is already in the watchlist. if so, don't add, and display a toast message informing the user it is already in the watchlist
        watchlist.add(film)
        Toast.makeText(this, "Added ${film.title} to Watchlist", Toast.LENGTH_SHORT).show()
    }

    override fun onMarkFilmWatched(film: FilmThumbnail) {
        //TODO("Not yet implemented")
    }
}


//fun createTestWatchlist(): ArrayList<FilmThumbnail> {
//    var resultList = ArrayList<FilmThumbnail>()
//    // Add some 'results' to the list
//    resultList.add(FilmThumbnail("Blade Runner", "", "tt0083658", "", "https://upload.wikimedia.org/wikipedia/en/thumb/9/9f/Blade_Runner_(1982_poster).png/220px-Blade_Runner_(1982_poster).png"))
//    resultList.add(FilmThumbnail("Predator", "", "tt0093773", "", "https://upload.wikimedia.org/wikipedia/en/9/95/Predator_Movie.jpg"))
//    resultList.add(FilmThumbnail("The Thing", "", "tt0084787", "", "https://upload.wikimedia.org/wikipedia/en/a/a6/The_Thing_(1982)_theatrical_poster.jpg"))
//    resultList.add(FilmThumbnail("The Fly", "", "tt0091064", "", "https://upload.wikimedia.org/wikipedia/en/a/aa/Fly_poster.jpg"))
//    return resultList
//}

