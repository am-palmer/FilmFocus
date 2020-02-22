package amichealpalmer.kotlin.filmfocus.activities

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.adapters.BrowseRecyclerAdapter
import amichealpalmer.kotlin.filmfocus.data.Film
import amichealpalmer.kotlin.filmfocus.data.FilmThumbnail
import android.app.Activity
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

//var watchlist: ArrayList<FilmThumbnail>, val name: String, val listener: MainActivity
class WatchlistActivity() : Activity() {

    private val TAG = "WatchlistActivity"
    private var watchlist = ArrayList<FilmThumbnail>()

    private var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        watchlist = intent.getParcelableArrayListExtra<FilmThumbnail>("thumbs")!!
        displayWatchlist()
    }

    private fun displayWatchlist() {
        Log.d(TAG, ".displayWatchlist called. getting FilmThumbnail list from intent")
        //val thumbs = intent.getParcelableArrayListExtra<FilmThumbnail>("thumbs")
        setContentView(R.layout.browse_films)// todo: inflate instead?
        recyclerView = findViewById<RecyclerView>(R.id.browse_films_recyclerview_id)
        recyclerView!!.layoutManager = GridLayoutManager(this, 3)
        recyclerView!!.adapter = BrowseRecyclerAdapter(this, watchlist)
      // registerForContextMenu()

    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, ".onContextItemSelected called")

        when (item.itemId) {
            R.id.film_thumbnail_context_menu_option1 -> Toast.makeText(this, "Option 1", Toast.LENGTH_SHORT).show()
            R.id.film_thumbnail_context_menu_option2 -> {
               // removeFilmFromWatchlist(
                val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
                val position = info.position
                val adapter = recyclerView!!.adapter as BrowseRecyclerAdapter
                removeFilmFromWatchlist(adapter.getItem(position))
                Toast.makeText(this, "Removed", Toast.LENGTH_SHORT).show()
            }
            else -> true
        }

        return super.onContextItemSelected(item)
    }

    private fun removeFilmFromWatchlist(film: FilmThumbnail) {
        watchlist.remove(film) // todo: this change has to be stored somewhere
        // Recall display
       displayWatchlist() // todo: destroys entire ui, try to refresh instead?
    }

}
