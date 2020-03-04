package amichealpalmer.kotlin.filmfocus.activities

//import android.R

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.adapters.BrowseRecyclerAdapter
import amichealpalmer.kotlin.filmfocus.data.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.data.json.GetJSONSearch
//import amichealpalmer.kotlin.filmfocus.fragments.ACTION_TYPE
import amichealpalmer.kotlin.filmfocus.fragments.BrowseFragment
import amichealpalmer.kotlin.filmfocus.fragments.FILM_CONTEXT_ACTION_TYPE
import amichealpalmer.kotlin.filmfocus.fragments.WatchlistFragment
import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.view.*


// todo: see trello

class MainActivity : AppCompatActivity(), WatchlistFragment.onFilmSelectedListener { // todo: disperse as much logic into the fragments as possible

    internal val OMDB_SEARCH_QUERY = "OMDB_SEACH_QUERY"
    internal val FILM_DETAILS_TRANSFER = "FILM_DETAILS_TRANSFER"

    val TAG = "MainActivity"
    //val testFilm = Film("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "")
    private lateinit var watchlist: ArrayList<FilmThumbnail>
    //private var recyclerView: RecyclerView? = null


    private var currentFragment: Fragment? = null // search or watchlist
    private lateinit var mDrawer: DrawerLayout
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

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

        // test watchlist create
        Log.d(TAG, ".onCreate: testing load of watchlist")
        watchlist = createTestWatchlist()
        watchlistHelper().inflateWatchlistFragment(watchlist)
        Log.d(TAG, ".onCreate finished")
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.d(TAG, ".onCreateOptionsMenu called")
        menuInflater.inflate(R.menu.options_menu, menu)

        // Associating searchable configuration with the SearchView
        val componentName = ComponentName(this, MainActivity::class.java)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        // Configuring the SearchView
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.isIconifiedByDefault = false
        searchView.requestFocus()

        return true
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

        //todo: replace with proper retreival functionality

        if (fragmentClass == WatchlistFragment::class.java) {
            fragment = fragmentClass.newInstance()
            val bundle = Bundle()
            bundle.putParcelableArrayList("watchlist", watchlist)
            fragment.arguments = bundle
        }

//        try {
//            fragment = fragmentClass.newInstance() as Fragment // not gonna work?
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }

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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d(TAG, ".onNewIntent called")
        if (Intent.ACTION_SEARCH == intent!!.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            Log.d(TAG, ".handleIntent: received new searchHelper query: $query")
            searchHelper().searchByTitleKeyword(query!!)
        } else {
            Log.d(TAG, "intent.action != Intent.ACTION_SEARCH")
        }
    }

//    private fun handleIntent(intent: Intent) {
//        Log.d(TAG, ".handleIntent started")
//        if (Intent.ACTION_SEARCH == intent.action) {
//            val query = intent.getStringExtra(SearchManager.QUERY)
//            Log.d(TAG, ".handleIntent: received new searchHelper query: $query")
//            searchHelper().searchByTitleKeyword(query!!)
//        } else {
//            Log.d(TAG, "intent.action != Intent.ACTION_SEARCH")
//        }
//    }

    fun onSearchResultsDownload(resultList: ArrayList<FilmThumbnail?>) {
        searchHelper().inflateSearchResultsFragment(resultList)
    }


    override fun onContextItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, ".onContextItemSelected called")
        Log.d(TAG, "item: ${item}")
        // todo: only works for watchlist right now! unsafe casts
        val fragment = currentFragment as WatchlistFragment
        val adapter = fragment.recyclerView!!.adapter as BrowseRecyclerAdapter
        var position = -1
        try {
            position = adapter.position
        } catch (e: java.lang.Exception) { // too general
            Log.d(TAG, e.localizedMessage, e)
            return super.onContextItemSelected(item)
        }
        when (item.itemId) { // todo: doesn't account for which fragment we're in!
            R.id.film_thumbnail_context_menu_option1 -> Toast.makeText(this, "Option 1", Toast.LENGTH_SHORT).show()
            R.id.film_thumbnail_context_menu_option2 -> {
                // removeFilmFromWatchlist(
                //val info = item.menuInfo as AdapterView.AdapterContextMenuInfo // todo: why the hell is this null?
                //val position = info.position
                watchlistHelper().removeFilmFromWatchlist(adapter.getItem(position))
                Toast.makeText(this, "Removed", Toast.LENGTH_SHORT).show()
            }
            else -> true
        }

        return super.onContextItemSelected(item)
    }

    private inner class watchlistHelper {
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


        fun removeFilmFromWatchlist(film: FilmThumbnail) {
            watchlist.remove(film) // todo: this change has to be stored somewhere
            // Recall display
            inflateWatchlistFragment(watchlist) // todo: destroys entire ui, try to refresh instead?
        }

    }

    private inner class searchHelper {
        fun searchByTitleKeyword(titleContains: String) {
            Log.d(TAG, ".searchByTitleKeyword starts")
            val query = "?s=$titleContains" // Indicates searchHelper by title
            GetJSONSearch(this@MainActivity, (this@MainActivity.getString(R.string.OMDB_API_KEY))).execute(query) // Call class handling API searchHelper queries
        }

        fun inflateSearchResultsFragment(resultList: ArrayList<FilmThumbnail?>) {
            Log.d(TAG, ".onSearchResultsDownload: JSON searchHelper calls listener")
            Log.d(TAG, ".onSearchResultsDownload: building fragment and replacing main_frame_layout_fragment_holder FrameLayout")

            // Build fragment, pass in data.
            //setContentView(R.layout.content_main)
            val fragment = BrowseFragment()
            var args = Bundle()
            args.putParcelableArrayList("resultList", resultList)
            fragment.arguments = args
            var transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.main_frame_layout_fragment_holder, fragment)
            transaction.commit()
        }

    }

    override fun onAttachFragment(fragment: Fragment) {
        if (fragment is WatchlistFragment) {
            fragment.setOnFilmSelectedListener(this)
        }
    }

    override fun onFilmSelected(position: Int, type: FILM_CONTEXT_ACTION_TYPE) { // todo: less idiosyncratic handling, less weird logic gates
        if (type == FILM_CONTEXT_ACTION_TYPE.WATCHLIST_REMOVE) {
            val watchlistFragment = currentFragment as WatchlistFragment
            val adapter = watchlistFragment.recyclerView.adapter as BrowseRecyclerAdapter
            watchlistHelper().removeFilmFromWatchlist(adapter.getItem(position))
            Toast.makeText(this, "Removed", Toast.LENGTH_SHORT).show()
        }
    }

}


fun createTestWatchlist(): ArrayList<FilmThumbnail> {
    var resultList = ArrayList<FilmThumbnail>()
    // Add some 'results' to the list
    resultList.add(FilmThumbnail("Blade Runner", "", "tt0083658", "", "https://upload.wikimedia.org/wikipedia/en/thumb/9/9f/Blade_Runner_(1982_poster).png/220px-Blade_Runner_(1982_poster).png"))
    resultList.add(FilmThumbnail("Predator", "", "tt0093773", "", "https://upload.wikimedia.org/wikipedia/en/9/95/Predator_Movie.jpg"))
    resultList.add(FilmThumbnail("The Thing", "", "tt0084787", "", "https://upload.wikimedia.org/wikipedia/en/a/a6/The_Thing_(1982)_theatrical_poster.jpg"))
    resultList.add(FilmThumbnail("The Fly", "", "tt0091064", "", "https://upload.wikimedia.org/wikipedia/en/a/aa/Fly_poster.jpg"))
    return resultList
}

