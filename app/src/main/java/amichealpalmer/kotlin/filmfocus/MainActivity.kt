package amichealpalmer.kotlin.filmfocus

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {


    val TAG = "MainActivity"
    val testFilm = Film("", "","", "","", "","", "","", "","", "", "", "", "", "", "")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        Log.d(TAG, "Set content view done")
        Log.d(TAG, "test call of FilmDetailsActivity")
       // FilmDetailsActivity(testFilm)

        fab.setOnClickListener { view -> // todo action button
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
        val search = Search(getString(R.string.OMDB_API_KEY), this) // Search should be a singleton?
        search.getFilmByID("tt0083658")
        // Test block for the film layout
        // Pass a film to the filmdetailsactivity class

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

    fun inflateFilmInformation(film: Film){
        Log.d(TAG, ".inflateFilmInformation starts")
        //setContentView(R.layout.activity_film_details)
        //FilmDetailsActivity(film)
        val intent = Intent(this, FilmDetailsActivity::class.java)
        intent.putExtra(FILM_DETAILS_TRANSFER, film)
        startActivity(intent)
        Log.d(TAG, ".inflateFilmInformation called")
    }
}
