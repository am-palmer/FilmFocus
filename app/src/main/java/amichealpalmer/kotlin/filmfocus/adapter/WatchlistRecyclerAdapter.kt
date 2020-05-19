package amichealpalmer.kotlin.filmfocus.adapter

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.model.entity.WatchlistItem
import amichealpalmer.kotlin.filmfocus.view.FilmActionListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class WatchlistRecyclerAdapter : ListAdapter<WatchlistItem, WatchlistRecyclerAdapter.WatchlistItemViewHolder>(DIFF_CALLBACK), Filterable {

    private var filmActionListener: FilmActionListener? = null
    //var position = 0
    // todo: reimplement filtering the list (Filterable interface) - have to use switchmap, probably in the watchlistfragment

    private lateinit var fullList: List<WatchlistItem>
    private var filteredList = ArrayList<WatchlistItem>()

    fun setFilmActionListener(listener: FilmActionListener){
        this.filmActionListener = listener
    }

    // We notify the adapter when the watchlist changes, for the purposes of filtering
    fun setWatchlist(watchlist: List<WatchlistItem>){
        fullList = watchlist
        notifyDataSetChanged()
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

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<WatchlistItem>(){
            override fun areItemsTheSame(oldItem: WatchlistItem, newItem: WatchlistItem): Boolean {
                return oldItem.imdbID == newItem.imdbID
            }

            override fun areContentsTheSame(oldItem: WatchlistItem, newItem: WatchlistItem): Boolean {
                return areItemsTheSame(oldItem, newItem)
            }
        }
    }

    // todo: reimplement this with livedata list
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                filteredList.clear()
                if (constraint == null || constraint.isEmpty()) {
                    filteredList.addAll(fullList)
                } else {
                    val pattern = constraint.toString().toLowerCase(Locale.US).trim()
                    for (item in fullList) {
                        if (item.title.toLowerCase(Locale.US).contains(pattern) || item.year.contains(pattern)) {
                            filteredList.add(item)
                        }
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList

                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                resultList.clear()
                resultList.addAll(results!!.values as ArrayList<FilmThumbnail>)
                notifyDataSetChanged()
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
                menu?.add(R.string.mark_watched)?.setOnMenuItemClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        filmActionListener?.markFilmWatched(getItem(position))
                    }
                    true
                }
                menu?.add(R.string.remove_from_watchlist)?.setOnMenuItemClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        filmActionListener?.removeFilmFromWatchlist(getItem(position))
                    }
                    true
                }
            }

        }

        fun displayPoster(posterURL: String) {
            Picasso.get().load(posterURL).error(R.drawable.ic_image_loading_grey_48dp)
                    .placeholder(R.drawable.ic_image_loading_grey_48dp).into(poster)
        }

    }

}