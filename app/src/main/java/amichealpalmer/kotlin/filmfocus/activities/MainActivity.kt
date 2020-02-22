package amichealpalmer.kotlin.filmfocus.activities

//import android.R
import amichealpalmer.kotlin.filmfocus.data.Film
import amichealpalmer.kotlin.filmfocus.data.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.R
import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

// todo: see trello

class MainActivity : BaseActivity() {


    val TAG = "MainActivity"
    val testFilm = Film("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "")
    //private val search = Search(this) //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        Log.d(TAG, "Set content view done")
        //   Log.d(TAG, "test call of FilmDetailsActivity")
        // FilmDetailsActivity(testFilm)

        fab.setOnClickListener { view ->
            // todo action button
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        // Test block for search
        //Log.d(TAG, "Now starting test search")
        //SearchResultsActivity.testSearch("spiderman") todo: fix
//
        // Test block for film lookup
//        Log.d(TAG, "Now starting test film lookup")
//        val search = Search(getString(R.string.OMDB_API_KEY), this) // Search should be a singleton?
//        search.getFilmByID("tt0083658")

        // test watchlist create
        createTestWatchlist()

        Log.d(TAG, ".onCreate finished")
    }

    // todo: test watchlist w/ this function
    fun createTestWatchlist() {
        var resultList = ArrayList<FilmThumbnail>()


        // Add some 'results' to the list
        resultList.add(FilmThumbnail("Blade Runner", "", "tt0083658", "", "https://upload.wikimedia.org/wikipedia/en/thumb/9/9f/Blade_Runner_(1982_poster).png/220px-Blade_Runner_(1982_poster).png"))
        resultList.add(FilmThumbnail("Predator", "", "tt0093773", "", "https://upload.wikimedia.org/wikipedia/en/9/95/Predator_Movie.jpg"))
        resultList.add(FilmThumbnail("The Thing", "", "tt0084787", "", "https://upload.wikimedia.org/wikipedia/en/a/a6/The_Thing_(1982)_theatrical_poster.jpg"))
        resultList.add(FilmThumbnail("The Fly", "", "tt0091064", "", "https://upload.wikimedia.org/wikipedia/en/a/aa/Fly_poster.jpg"))

        // Pass the 'results' to the watchlist activity
        intent = Intent(this, WatchlistActivity::class.java)
        intent.putParcelableArrayListExtra("thumbs", resultList)
        //intent.putExtra("displaycontext", DisplayContext.WATCHLIST)
        startActivity(intent)

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.options_menu, menu)

        // Associating searchable configuration with the SearchView
        val componentName = ComponentName(this, SearchActivity::class.java)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        // Configuring the SearchView
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.isIconifiedByDefault = false
        searchView.requestFocus()
//        (menu.findItem(R.id.search).actionView as SearchView).apply {
//            setSearchableInfo(searchManager.getSearchableInfo(componentName))
//
//            requestFocus()
//        }


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

//
//    fun inflateFilmInformation(film: Film){
//        Log.d(TAG, ".inflateFilmInformation starts")
//        //setContentView(R.layout.activity_film_details)
//        //FilmDetailsActivity(film)
//        val intent = Intent(this, FilmDetailsActivity::class.java)
//        intent.putExtra(FILM_DETAILS_TRANSFER, film)
//        startActivity(intent)
//        Log.d(TAG, ".inflateFilmInformation called")
//    }

