package amichealpalmer.kotlin.filmfocus

//import android.R
import android.app.Activity
import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.browse_films.*

// todo: see trello

class MainActivity : BaseActivity() {


    val TAG = "MainActivity"
    val testFilm = Film("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "")
    //private val search = Search(this) //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
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


        Log.d(TAG, ".onCreate finished")
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.options_menu, menu)

        // Associating searchable configuration with the SearchView
        val componentName = ComponentName(this, SearchResultsActivity::class.java)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.search).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
        }

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

class SearchResultsActivity : Activity() { // todo: own class. make the search less buggy -> pressing back too much?

    private val TAG = "SearchResultsActivity"
    private val search = Search(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, ".onCreate called")
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
       // super.onNewIntent(intent)
        Log.d(TAG, ".onNewIntent called")
        handleIntent(intent)
    }

    fun testSearch(query: String) {
        search.searchByTitleKeyword(query)
    }

    private fun handleIntent(intent: Intent) {
        Log.d(TAG, ".handleIntent started")
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            Log.d(TAG, ".handleIntent: received new search query: $query")
            // todo: search with the query
            search.searchByTitleKeyword(query)

        } else {
            Log.d(TAG, "intent.action != Intent.ACTION_SEARCH")
        }
    }

    fun displaySearchResults(resultList: List<GetJSONSearch.Result>) { // todo: the result display is screwy. browse films should include app bar?
        // Testing results view
        //val myrv = findViewById(R.id.recyclerview_id) as RecyclerView
        Log.d(TAG, ".displaySearchResults called. Attempting to display search result list")
        setContentView(R.layout.browse_films)// todo: inflate instead?
        //layoutInflater.inflate(R.layout.browse_films, )
        val myAdapter = BrowseRecyclerAdapter(this, resultList) // cast may cause issues if so modify class
        val recyclerView = findViewById<RecyclerView>(R.id.browse_films_recyclerview_id)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.adapter = myAdapter
        //setContentView(R.layout.activity_main)
        Log.d(TAG, ".displaySearchResults complete")
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

