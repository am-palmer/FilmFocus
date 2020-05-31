package amichealpalmer.kotlin.filmfocus


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.view.*

class MainActivity : AppCompatActivity() {

    private var appBarConfiguration: AppBarConfiguration? = null

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
    }

}