package amichealpalmer.kotlin.filmfocus.activities

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.adapters.BrowseRecyclerAdapter
import amichealpalmer.kotlin.filmfocus.data.FilmThumbnail
import android.app.Activity
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

//var watchlist: ArrayList<FilmThumbnail>, val name: String, val listener: MainActivity
class WatchlistActivity() : Activity() {

    private val TAG = "WatchlistActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        displayWatchlist()
    }

    private fun displayWatchlist() {
        Log.d(TAG, ".displayWatchlist called. getting FilmThumbnail list from intent")
        val thumbs = intent.getParcelableArrayListExtra<FilmThumbnail>("thumbs")
        setContentView(R.layout.browse_films)// todo: inflate instead?
        val recyclerView = findViewById<RecyclerView>(R.id.browse_films_recyclerview_id)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.adapter = BrowseRecyclerAdapter(this, thumbs)


    }

}
