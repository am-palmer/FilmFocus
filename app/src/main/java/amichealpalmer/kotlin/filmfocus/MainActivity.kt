package amichealpalmer.kotlin.filmfocus


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.toolbar.*

// todo: constraint or other relative layout in the activity xml
// todo: landscape layout for activity (?)

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // Navigation with BottomNavigationView
        val botNavView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        var navController = findNavController(R.id.activity_nav_host_fragment)
        NavigationUI.setupWithNavController(botNavView, navController)

    }

}