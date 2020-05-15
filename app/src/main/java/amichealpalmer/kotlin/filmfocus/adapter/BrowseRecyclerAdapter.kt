package amichealpalmer.kotlin.filmfocus.adapter

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.view.FilmDetailDialogFragment
import android.view.*
import android.widget.AdapterView
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class BrowseRecyclerAdapter : ListAdapter<FilmThumbnail, BrowseRecyclerAdapter.FilmThumbnailViewHolder>(DIFF_CALLBACK) {

    private var viewFilmDetailsListener: ViewFilmDetailsListener? = null
    private var addFilmToWatchlistListener: AddFilmToWatchlistListener? = null
    private var markFilmWatchedListener: MarkFilmWatchedListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmThumbnailViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.browse_films_item, parent, false)
        return FilmThumbnailViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilmThumbnailViewHolder, position: Int) {
        val currentItem: FilmThumbnail = getItem(position)
        holder.loadPoster(currentItem.posterURL)
    }

    fun getFilmThumbnailAtPosition(position: Int): FilmThumbnail {
        return getItem(position)
    }

    companion object { // todo: necessary?
        private const val TAG = "BrowseRecyclerAdapter"
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FilmThumbnail>() {
            override fun areItemsTheSame(oldItem: FilmThumbnail, newItem: FilmThumbnail): Boolean {
                return oldItem.imdbID == newItem.imdbID
            }

            override fun areContentsTheSame(oldItem: FilmThumbnail, newItem: FilmThumbnail): Boolean {
                return oldItem.imdbID == newItem.imdbID
            }
        }
    }

    inner class FilmThumbnailViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val poster: ImageView = view.findViewById(R.id.film_poster_id)
        private val cardView = view.findViewById<CardView>(R.id.film_item_cardview_id)

        init {

            cardView.setOnClickListener {
                // Display FilmDetailsDialogFragment
                // todo: this has to happen in the fragment
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    viewFilmDetailsListener.viewFilm(getItem(position))
                }
                //todo: move this call to the fragment implementing the listener
                //val fragment = FilmDetailDialogFragment.newInstance(resultList[adapterPosition].imdbID)
                //fragment.show(parent.get()!!.childFragmentManager, FilmDetailDialogFragment.TAG)
            }

            cardView.setOnCreateContextMenuListener { menu, v, menuInfo ->
                menu?.add(R.string.add_to_watchlist)?.setOnMenuItemClickListener {
                    // todo: notify that we want to add the item to the watchlist (if it isn't already present)
                    true
                }
                menu?.add(R.string.mark_watched)?.setOnMenuItemClickListener {
                    // todo: show the mark watched dialog
                    true
                }
            }
        }

        fun loadPoster(posterURL: String) {
            Picasso.get().load(posterURL).error(R.drawable.ic_image_loading_darkgreen_48dp)
                    .placeholder(R.drawable.ic_image_loading_darkgreen_48dp).into(poster)
        }

    }

}