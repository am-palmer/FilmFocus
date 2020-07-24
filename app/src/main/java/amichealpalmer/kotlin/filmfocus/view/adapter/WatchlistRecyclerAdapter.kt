package amichealpalmer.kotlin.filmfocus.view.adapter

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.databinding.WatchlistListItemBinding
import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.model.entity.WatchlistItem
import amichealpalmer.kotlin.filmfocus.view.listener.WatchlistActionListener
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class WatchlistRecyclerAdapter : ListAdapter<WatchlistItem, WatchlistRecyclerAdapter.WatchlistItemViewHolder>(DIFF_CALLBACK) {

    private var watchlistActionListener: WatchlistActionListener? = null
    private var fullList = listOf<WatchlistItem>()

    fun setFilmActionListener(listener: WatchlistActionListener) {
        this.watchlistActionListener = listener
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
        return WatchlistItemViewHolder(WatchlistListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: WatchlistItemViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)

    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<WatchlistItem>() {
            override fun areItemsTheSame(oldItem: WatchlistItem, newItem: WatchlistItem): Boolean {
                return areContentsTheSame(oldItem, newItem)
            }

            override fun areContentsTheSame(oldItem: WatchlistItem, newItem: WatchlistItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class WatchlistItemViewHolder(private val binding: WatchlistListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FilmThumbnail) {
            binding.apply {
                film = item

                watchlistFilmPoster.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        watchlistActionListener?.showFilmDetails(getItem(position))
                    }
                }

                watchlistFilmPoster.setOnCreateContextMenuListener { menu, _, _ ->
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        menu?.add(R.string.mark_watched)?.setOnMenuItemClickListener {
                            watchlistActionListener?.markFilmWatched(getItem(position))
                            true
                        }
                        menu?.add(R.string.remove_from_watchlist)?.setOnMenuItemClickListener {
                            watchlistActionListener?.removeFilmFromWatchlist(getItem(position))
                            true
                        }
                    }
                }

                executePendingBindings()
            }
        }

    }

}