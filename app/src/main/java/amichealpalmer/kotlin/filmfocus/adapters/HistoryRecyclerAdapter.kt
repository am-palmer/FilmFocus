package amichealpalmer.kotlin.filmfocus.adapters


import amichealpalmer.kotlin.filmfocus.data.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.data.TimelineItem
//import amichealpalmer.kotlin.filmfocus.activities.BrowseActivity
//import amichealpalmer.kotlin.filmfocus.activities.WatchlistActivity
import amichealpalmer.kotlin.filmfocus.fragments.FilmDetailsFragment
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
//import android.support.v7.widget.CardView
//import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.joda.time.LocalDate
import org.w3c.dom.Text


class HistoryRecyclerAdapter(
        private val context: Context,
        private var timelineList: ArrayList<TimelineItem> // List of items on the timeline
) : RecyclerView.Adapter<HistoryRecyclerAdapter.HelperViewHolder>() {

    private val TAG = "HistoryRecyclerAdapter"
    var position = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelperViewHolder {
        Log.d(TAG, ".onCreateViewHolder called")
        val view: View
        val mInflater = LayoutInflater.from(context)
        view = mInflater.inflate(R.layout.browse_films_item, parent, false)

        return HelperViewHolder(view)
    }

    fun updateList(timelineList: ArrayList<TimelineItem>) {
        this.timelineList.addAll(timelineList)
        notifyDataSetChanged()
    }

    fun clearList() {
        timelineList.clear()
    }

    override fun onBindViewHolder(holder: HelperViewHolder, position: Int) {
        if (timelineList.size > 0) {
            Picasso.get().load(timelineList[position].film.posterURL).error(R.drawable.placeholder_imageloading)
                    .placeholder(R.drawable.placeholder_imageloading).into(holder.poster)

            val date = timelineList[position].date as LocalDate

            // Set the views
            holder.dateYearTextView.text = date.year.toString()
            holder.dateMonthTextView.text = date.monthOfYear.toString()
            holder.dateDayTextView.text = date.dayOfMonth.toString()

            if (timelineList[position].rating != null){
                holder.ratingBar.rating = 0.toFloat() // Rating is blank. todo: different xml layouts for items or programatically remove rating / review if not included
            } else {
                holder.ratingBar.rating = timelineList[position].rating!!.toFloat()
            }

            if (timelineList[position].hasReview()){
                holder.reviewTextView.text = timelineList[position].getReview()
            } else holder.reviewTextView.text = "" // todo: again, progromatically remove view if field is null

            holder.itemView.setOnLongClickListener {
                this.position = (holder.adapterPosition)
                false
            }
        }
    }

    override fun onViewRecycled(holder: HelperViewHolder) {
        holder.itemView.setOnLongClickListener(null)
        super.onViewRecycled(holder)
    }

    fun getItem(position: Int): TimelineItem {
        return timelineList[position]
    }


    override fun getItemCount(): Int {
        return timelineList.size
    }

    inner class HelperViewHolder(view: View)
        : RecyclerView.ViewHolder(view), View.OnCreateContextMenuListener {
        val poster: ImageView = view.findViewById(R.id.timeline_item_film_poster)
        val cardViewWrapper: CardView = view.findViewById(R.id.timeline_item_cardview_wrapper)
        val ratingBar: RatingBar = view.findViewById(R.id.timeline_item_ratingBar)
        val dateDayTextView: TextView = view.findViewById(R.id.timeline_item_date_day)
        val dateMonthTextView: TextView = view.findViewById(R.id.timeline_item_date_month)
        val dateYearTextView: TextView = view.findViewById(R.id.timeline_item_date_year)
        val reviewTextView: TextView = view.findViewById(R.id.timeline_item_review_tv)

        init {
            poster.setOnClickListener {

                val fragment = FilmDetailsFragment()
                val bundle = Bundle()
                bundle.putString("imdbID", timelineList[adapterPosition].film.imdbID)
                fragment.arguments = bundle
                val manager = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                manager.setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                manager.addToBackStack(null)
                manager.replace(R.id.main_frame_layout_fragment_holder, fragment).commit()
            }

            cardViewWrapper.setOnCreateContextMenuListener(this)
            poster.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(menu: ContextMenu?, v: View, menuInfo: ContextMenu.ContextMenuInfo?) { // does not return true and performs normal click. todo fix
            val inflater = MenuInflater(context)
            inflater.inflate(R.menu.history_timeline_item_context_menu, menu)
        }


    }

}