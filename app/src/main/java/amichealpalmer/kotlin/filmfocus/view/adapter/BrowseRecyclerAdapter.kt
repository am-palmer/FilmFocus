package amichealpalmer.kotlin.filmfocus.view.adapter

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.databinding.BrowseFilmsItemBinding
import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.view.listener.BrowseActionListener
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class BrowseRecyclerAdapter : ListAdapter<FilmThumbnail, BrowseRecyclerAdapter.FilmThumbnailViewHolder>(DIFF_CALLBACK) {

    private var browseActionListener: BrowseActionListener? = null

    fun setFilmActionListener(listener: BrowseActionListener) {
        this.browseActionListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmThumbnailViewHolder {
        return FilmThumbnailViewHolder(BrowseFilmsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: FilmThumbnailViewHolder, position: Int) {
        val currentItem: FilmThumbnail = getItem(position)
        holder.bind(currentItem)
    }

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FilmThumbnail>() {
            override fun areItemsTheSame(oldItem: FilmThumbnail, newItem: FilmThumbnail): Boolean {
                return oldItem.imdbID == newItem.imdbID
            }

            override fun areContentsTheSame(oldItem: FilmThumbnail, newItem: FilmThumbnail): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class FilmThumbnailViewHolder(private val binding: BrowseFilmsItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FilmThumbnail) {
            binding.apply {
                film = item

                browseFilmPoster.setOnClickListener {
                    // Display FilmDetailsDialogFragment
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        browseActionListener?.showFilmDetails(getItem(position))
                    }
                }

                browseFilmPoster.setOnCreateContextMenuListener { menu, _, _ ->
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        menu?.add(R.string.add_to_watchlist)?.setOnMenuItemClickListener {
                            browseActionListener?.addFilmToWatchlist(getItem(position))
                            true
                        }
                        menu?.add(R.string.mark_watched)?.setOnMenuItemClickListener {
                            browseActionListener?.markFilmWatched(getItem(position))
                            true
                        }
                    }
                }

                executePendingBindings()
            }
        }

    }

}