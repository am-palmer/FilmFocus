package amichealpalmer.kotlin.filmfocus.adapter

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
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

class BrowseRecyclerAdapter : ListAdapter<FilmThumbnail, BrowseRecyclerAdapter.FilmThumbnailViewHolder>(DIFF_CALLBACK) {

    private var filmActionListener: FilmActionListener? = null

    fun setFilmActionListener(listener: FilmActionListener) {
        this.filmActionListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmThumbnailViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.browse_films_item, parent, false)
        return FilmThumbnailViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilmThumbnailViewHolder, position: Int) {
        val currentItem: FilmThumbnail = getItem(position)
        holder.displayPoster(currentItem.posterURL)
    }

    fun getFilmThumbnailAtPosition(position: Int): FilmThumbnail {
        return getItem(position)
    }

    companion object {
        private const val TAG = "BrowseRecyclerAdapter"

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FilmThumbnail>() {
            override fun areItemsTheSame(oldItem: FilmThumbnail, newItem: FilmThumbnail): Boolean {
                return oldItem.imdbID == newItem.imdbID
            }

            override fun areContentsTheSame(oldItem: FilmThumbnail, newItem: FilmThumbnail): Boolean {
                return areItemsTheSame(oldItem, newItem)
            }
        }
    }

    inner class FilmThumbnailViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        // todo: synthetic imports
        private val poster: ImageView = view.findViewById(R.id.film_poster_id)
        private val cardView = view.findViewById<CardView>(R.id.film_item_cardview_id)

        init {

            cardView.setOnClickListener {
                // Display FilmDetailsDialogFragment
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    filmActionListener?.showFilmDetails(getItem(position))
                }
            }

            cardView.setOnCreateContextMenuListener { menu, v, menuInfo ->
                Log.d(TAG, "createContextmenuListener triggered")
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    menu?.add(R.string.add_to_watchlist)?.setOnMenuItemClickListener {
                        filmActionListener?.addFilmToWatchlist(getItem(position))
                        true
                    }
                    menu?.add(R.string.mark_watched)?.setOnMenuItemClickListener {
                        filmActionListener?.markFilmWatched(getItem(position))
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