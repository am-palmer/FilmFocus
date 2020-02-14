package amichealpalmer.kotlin.filmfocus

import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    // private val apikey = getString(R.string.OMDB_API_KEY)
    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // todo: debug async tasks -> process trapped somewhere
        setSupportActionBar(toolbar)
        Log.d(TAG, "Set content view done")

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        // Test block for search
//        Log.d(TAG, "Now starting test search")
//        val search = Search(getString(R.string.OMDB_API_KEY))
//        search.searchByTitleKeyword("spiderman")
//
        // Test block for film lookup
        Log.d(TAG, "Now starting test film lookup")
        val search = Search(getString(R.string.OMDB_API_KEY)) // Search should be a singleton?
        search.getFilmByID("tt0083658")


        // setContentView(R.layout.activity_film_details)


        Log.d(TAG, ".onCreate finished")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
