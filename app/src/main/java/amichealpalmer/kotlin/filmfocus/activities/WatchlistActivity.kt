package amichealpalmer.kotlin.filmfocus.activities

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.adapters.BrowseRecyclerAdapter
import amichealpalmer.kotlin.filmfocus.data.Film
import amichealpalmer.kotlin.filmfocus.data.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.fragments.BrowseFragment
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
import java.lang.Exception

class WatchlistActivity : BrowseActivity() {

    private val TAG = "WatchlistActivity"
    private lateinit var watchlist: ArrayList<FilmThumbnail>

    private var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        watchlist = intent.getParcelableArrayListExtra<FilmThumbnail>("thumbs")!!
        displayWatchlist()
    }

    private fun displayWatchlist() {
//        Log.d(TAG, ".displayWatchlist called. getting FilmThumbnail list from intent")
//        setContentView(R.layout.browse_films)// todo: inflate instead?
//        recyclerView = findViewById<RecyclerView>(R.id.browse_films_recyclerview_id)
//        recyclerView!!.layoutManager = GridLayoutManager(this, 3)
//        recyclerView!!.adapter = BrowseRecyclerAdapter(this, watchlist)
        // Make a super call to display the resultList
        super.displayBrowseFragment(watchlist as ArrayList<FilmThumbnail?>)

    }


    override fun onContextItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, ".onContextItemSelected called")
        Log.d(TAG, "item: ${item}")
        val adapter = recyclerView!!.adapter as BrowseRecyclerAdapter
        var position = -1
        try {
            position = adapter.position
        } catch (e: Exception) { // too general
            Log.d(TAG, e.localizedMessage, e)
            return super.onContextItemSelected(item)
        }
        when (item.itemId) {
            R.id.film_thumbnail_context_menu_option1 -> Toast.makeText(this, "Option 1", Toast.LENGTH_SHORT).show()
            R.id.film_thumbnail_context_menu_option2 -> {
                // removeFilmFromWatchlist(
                //val info = item.menuInfo as AdapterView.AdapterContextMenuInfo // todo: why the hell is this null?
                //val position = info.position
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
