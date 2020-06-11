package amichealpalmer.kotlin.filmfocus.view.adapter

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.model.entity.WatchlistItem
import amichealpalmer.kotlin.filmfocus.view.FilmActionListener
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.util.*

class WatchlistRecyclerAdapter : ListAdapter<WatchlistItem, WatchlistRecyclerAdapter.WatchlistItemViewHolder>(DIFF_CALLBACK) {

    private var filmActionListener: FilmActionListener? = null
    private var fullList = listOf<WatchlistItem>()

    fun setFilmActionListener(listener: FilmActionListener) {
        this.filmActionListener = listener
    }

    // We notify the adapter when the Watchlist changes, for the purposes of filtering
    fun modifyList(list: List<WatchlistItem>?) {
        fullList = list ?: listOf()
        submitList(list)
    }

    // Used by the searchView in WatchlistFragment to filter the items in the watchlist
    fun filter(query: CharSequence?) {
        val pattern = query.toString().toLowerCase(Locale.US).trim()
        val list = mutableListOf<WatchlistItem>()
        if (pattern.isNotEmpty()) {
            list.addAll(fullList.filter { it.title.toLowerCase(Locale.US).contains(pattern) })
        } else {
            list.addAll(fullList)
        }
        submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchlistItemViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.browse_films_item, parent, false)
        return WatchlistItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: WatchlistItemViewHolder, position: Int) {
        val currentItem: WatchlistItem = getItem(position)
        holder.displayPoster(currentItem.posterURL)
    }

    companion object {
        private const val TAG = "WatchlistRecyclerAdapt"

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<WatchlistItem>() {
            override fun areItemsTheSame(oldItem: WatchlistItem, newItem: WatchlistItem): Boolean {
                return areContentsTheSame(oldItem, newItem)
            }

            override fun areContentsTheSame(oldItem: WatchlistItem, newItem: WatchlistItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class WatchlistItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val poster: ImageView = view.findViewById(R.id.film_poster_id)
        private val cardView: CardView = view.findViewById(R.id.film_item_cardview_id)

        init {

            cardView.setOnClickListener {
                // Display FilmDetailsDialogFragment
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    filmActionListener?.showFilmDetails(getItem(position))
                }
            }

            cardView.setOnCreateContextMenuListener { menu, v, menuInfo ->
                Log.d(TAG, "long clicked context menu")
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    menu?.add(R.string.mark_watched)?.setOnMenuItemClickListener {
                        filmActionListener?.markFilmWatched(getItem(position))
                        true
                    }
                    menu?.add(R.string.remove_from_watchlist)?.setOnMenuItemClickListener {
                        filmActionListener?.removeFilmFromWatchlist(getItem(position))
                        true
                    }
                }

            }

        }

        fun displayPoster(posterURL: String) {
            Picasso.get().load(posterURL).error(R.drawable.ic_image_loading_grey_48dp)
                    .placeholder(R.drawable.ic_image_loading_grey_48dp).into(poster)
        }

    }

}