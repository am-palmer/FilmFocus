package amichealpalmer.kotlin.filmfocus.adapters


import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.data.RATING_VALUE
import amichealpalmer.kotlin.filmfocus.data.TIMELINE_ITEM_STATUS
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.joda.time.LocalDate


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
        view = mInflater.inflate(R.layout.history_list_item, parent, false)

        return HelperViewHolder(view)
    }

    fun updateList(timelineList: ArrayList<TimelineItem>) {
        this.timelineList.addAll(timelineList)
        notifyDataSetChanged()
    }

    fun removeTimelineItem(item: TimelineItem){
        timelineList.remove(item)
    }

    fun clearList() {
        timelineList.clear()
    }

    override fun onBindViewHolder(holder: HelperViewHolder, position: Int) {
        if (timelineList.size > 0) {
            Picasso.get().load(timelineList[position].film.posterURL).error(R.drawable.placeholder_imageloading)
                    .placeholder(R.drawable.placeholder_imageloading).into(holder.poster)

            var date = timelineList[position].date
            //val dayProperty: LocalDate.Property = date.dayOfWeek()
            val monthProperty: LocalDate.Property = date.monthOfYear()
            // Set the views
            // holder.dateYearTextView.text = date.year.toString()
            holder.dateMonthTextView.text = date.dayOfMonth.toString()
            holder.dateDayTextView.text = monthProperty.asText
            if (timelineList[position].rating.state == RATING_VALUE.NO_RATING) {
                holder.ratingBar.visibility = View.GONE // Hide the rating bar if a rating hasn't been set
                // We also change the constraints on the review so there isn't a weird gap
                val constraintSet = ConstraintSet()
                constraintSet.clone(holder.constraintLayoutWrapper)
                constraintSet.connect(holder.reviewTextView.id, ConstraintSet.TOP, holder.dateHolderConstraintLayout.id, ConstraintSet.BOTTOM, 12)
                constraintSet.applyTo(holder.constraintLayoutWrapper)
            } else {
                holder.ratingBar.rating = timelineList[position].rating.value
            }

            if (timelineList[position].hasReview()) {
                val review = "\"" + timelineList[position].getReview() + "\""
                holder.reviewTextView.text = review
            } else {
                holder.reviewTextView.visibility = View.GONE // Hide review field if not set
            }
            // Programmatically change views if film is marked as dropped
            if (timelineList[position].status == TIMELINE_ITEM_STATUS.DROPPED){
                holder.watchedDroppedIcon.setImageResource(R.drawable.ic_dropped_darkgreen_24dp)
                holder.watchedDroppedTextView.text = "Dropped"
            }

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

        //val cardViewWrapper: CardView = view.findViewById(R.id.timeline_item_cardview_wrapper)
        val constraintLayoutWrapper: ConstraintLayout = view.findViewById(R.id.history_timeline_item_constraintLayout)
        val ratingBar: RatingBar = view.findViewById(R.id.timeline_item_ratingBar)
        val dateDayTextView: TextView = view.findViewById(R.id.timeline_item_date_day)
        val dateMonthTextView: TextView = view.findViewById(R.id.timeline_item_date_month)
        val dateHolderConstraintLayout: ConstraintLayout = view.findViewById(R.id.date_holder_constraintLayout)
        val watchedDroppedTextView: TextView = view.findViewById(R.id.timeline_item_tv_WATCHED_DROPPED)
        val watchedDroppedIcon: ImageView = view.findViewById(R.id.icon_watched_dropped_drawable)
        //val dateYearTextView: TextView = view.findViewById(R.id.timeline_item_date_year)
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

            //constraintLayoutWrapper.setOnCreateContextMenuListener(this)
            poster.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(menu: ContextMenu?, v: View, menuInfo: ContextMenu.ContextMenuInfo?) { // does not return true and performs normal click. todo fix
            val inflater = MenuInflater(context)
            inflater.inflate(R.menu.history_timeline_item_context_menu, menu)
        }


    }

}