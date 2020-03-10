package amichealpalmer.kotlin.filmfocus.adapters


import amichealpalmer.kotlin.filmfocus.data.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.data.Film
import amichealpalmer.kotlin.filmfocus.fragments.FilmDetailsFragment
import android.app.Activity
import android.app.FragmentTransaction
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

// todo: could inherit / reduce code using browserecycleradapter
class WatchlistRecyclerAdapter(
        private val context: Context,
        private var resultList: ArrayList<FilmThumbnail> // The list of films currently being displayed in the browser
) : RecyclerView.Adapter<WatchlistRecyclerAdapter.HelperViewHolder>(), Filterable {

    private val TAG = "WatchlistRecyclerAdapt"
    var position = 0
    private val fullList = ArrayList<FilmThumbnail>(resultList)
    private var filteredList = ArrayList<FilmThumbnail>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelperViewHolder {
        Log.d(TAG, ".onCreateViewHolder called")
        val view: View
        val mInflater = LayoutInflater.from(context)
        view = mInflater.inflate(R.layout.browse_films_item, parent, false)

        return HelperViewHolder(view)
    }


    override fun onBindViewHolder(holder: HelperViewHolder, position: Int) {
        //Log.d(TAG, ".onBindViewHolder called. Title of film is: ${resultList[position].title}")
        if (resultList.size > 0) {
            Picasso.get().load(resultList[position].posterURL).error(R.drawable.placeholder_imageloading)
                    .placeholder(R.drawable.placeholder_imageloading).into(holder.poster)

            holder.itemView.setOnLongClickListener {
                this.position = (holder.adapterPosition)
                false
            }
        }
    }

    override fun onViewRecycled(holder: HelperViewHolder) {
        // may not be required
        holder.itemView.setOnLongClickListener(null)
        super.onViewRecycled(holder)

    }

    fun getItem(position: Int): FilmThumbnail {
        return resultList[position]
    }


    override fun getItemCount(): Int {
        return resultList.size
    }

    fun removeFilmFromWatchlist(film: FilmThumbnail){
        resultList.remove(film)
        fullList.remove(film)
        filteredList.remove(film)
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                //val filteredList = ArrayList<FilmThumbnail>()
                filteredList.clear()
                if (constraint == null || constraint.length == 0) {
                    filteredList.addAll(fullList)
                } else {
                    val pattern = constraint.toString().toLowerCase().trim()
                    for (item in fullList) {
                        if (item.title.toLowerCase().contains(pattern) || item.year.contains(pattern)) {
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



    inner class HelperViewHolder(view: View)
        : RecyclerView.ViewHolder(view), View.OnCreateContextMenuListener {
        val poster: ImageView = view.findViewById(R.id.film_poster_id)
        val cardView: CardView = view.findViewById(R.id.film_item_cardview_id)

        init {
            cardView.setOnClickListener {

                // Using fragment
                val fragment = FilmDetailsFragment()
                val bundle = Bundle()
                bundle.putString("imdbID", resultList[adapterPosition].imdbID)
                fragment.arguments = bundle
                val manager = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                manager.setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                manager.addToBackStack(null)
                manager.replace(R.id.main_frame_layout_fragment_holder, fragment).commit()
            }

            cardView.setOnCreateContextMenuListener(this)

        }

        override fun onCreateContextMenu(menu: ContextMenu?, v: View, menuInfo: ContextMenu.ContextMenuInfo?) { // does not return true and performs normal click. todo fix
            Log.d(TAG, ".onCreateContextMenu called.")
            val inflater = MenuInflater(context)
            inflater.inflate(R.menu.film_thumbnail_context_menu, menu)

        }


    }

}