package amichealpalmer.kotlin.filmfocus


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.view.*

// todo: we need viewmodel
// todo: use alertdialogs instead of dialogfragments for basic yes/no dialogs
class MainActivity : AppCompatActivity() {

    private var appBarConfiguration: AppBarConfiguration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // Must initialize these fields before super call so they are available for fragments
//        timelineSharedPrefUtil = TimelineItemsSharedPrefUtil(this)
//        watchlistSharedPrefUtil = WatchlistSharedPrefUtil(this)

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

//
//    override fun onAttachFragment(fragment: Fragment) {
//        if (fragment is WatchlistFragment) {
//            fragment.setWatchlistFragmentDataListener(this)
//        }
//        if (fragment is BrowseFragment) {
//           // fragment.setOnResultActionListener(this)
//        }
//        if (fragment is HistoryFragment) {
//            fragment.setOnTimelineItemSelectedListener(this)
//        }
//    }

    companion object{
        private const val TAG = "MainActivity"
    }

}


