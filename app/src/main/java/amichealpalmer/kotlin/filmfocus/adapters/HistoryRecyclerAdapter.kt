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

    fun removeTimelineItem(item: TimelineItem) {
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
            val dayInt = date.dayOfMonth.toInt()
            var dateDay = "null"

            // Figure out the ordinal indicator
            var j = dayInt % 10
            var k = dayInt % 100
            if (j == 1 && k != 11) {
                dateDay = dayInt.toString() + "st"
            } else if (j == 2 && k != 12) {
                dateDay = dayInt.toString() + "nd"
            } else if (j == 3 && k != 13) {
                dateDay = dayInt.toString() + "rd"
            } else {
                dateDay = dayInt.toString() + "th"
            }

            holder.dateMonthTextView.text = monthProperty.asText
            holder.dateDayTextView.text = dateDay
            if (timelineList[position].rating.state == RATING_VALUE.NO_RATING || timelineList[position].rating.value == 0f) {
                holder.ratingBar.visibility = View.GONE // Hide the rating bar if a rating hasn't been set
                // We also change the constraints on the review so there isn't a weird gap
                val constraintSet = ConstraintSet()
                constraintSet.clone(holder.constraintLayoutWrapper)
                constraintSet.connect(holder.reviewTextView.id, ConstraintSet.TOP, R.id.timeline_item_film_poster, ConstraintSet.TOP, 0)
                constraintSet.setVerticalBias(R.id.timeline_item_review_tv, 0.50f)

                constraintSet.applyTo(holder.constraintLayoutWrapper)
            } else {
                holder.ratingBar.rating = timelineList[position].rating.value
                holder.ratingBar.visibility = View.VISIBLE
            }

            if (timelineList[position].hasReview()) {
                val review = "\"" + timelineList[position].getReview() + "\""
                holder.reviewTextView.text = review
                holder.reviewTextView.visibility = View.VISIBLE
            } else {
                holder.reviewTextView.visibility = View.GONE // Hide review field if not set
                if (timelineList[position].rating.value > 0f) {
                    val constraintSet = ConstraintSet()
                    constraintSet.clone(holder.constraintLayoutWrapper)
                    constraintSet.setVerticalBias(R.id.timeline_item_ratingBar, 0.50f)
                    constraintSet.applyTo(holder.constraintLayoutWrapper)
                }
            }

            // Programmatically remove portion of line if this is the first or last entry in the list
            if (position == 0) {
                holder.timelineLineTop.visibility = View.INVISIBLE
            } else if (position == (timelineList.size) - 1) {
                holder.timelineLineBottom.visibility = View.INVISIBLE
            }

            // Programmatically change views if film is marked as watched or dropped
            when (timelineList[position].status) {
                TIMELINE_ITEM_STATUS.WATCHED -> {
                    holder.watchedDroppedTextView.text = "Watched"
                    holder.watchedDroppedIcon.setImageResource(R.drawable.ic_watched_darkgreen_24dp)
                }
                TIMELINE_ITEM_STATUS.DROPPED -> {
                    holder.watchedDroppedIcon.setImageResource(R.drawable.ic_dropped_darkgreen_24dp)
                    holder.watchedDroppedTextView.text = "Dropped"
                }
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

        val constraintLayoutWrapper: ConstraintLayout = view.findViewById(R.id.history_timeline_item_constraintLayout)
        val ratingBar: RatingBar = view.findViewById(R.id.timeline_item_ratingBar)
        val dateDayTextView: TextView = view.findViewById(R.id.timeline_item_date_day)
        val dateMonthTextView: TextView = view.findViewById(R.id.timeline_item_date_month)

        //val dateHolderConstraintLayout: ConstraintLayout = view.findViewById(R.id.date_holder_constraintLayout)
        val watchedDroppedTextView: TextView = view.findViewById(R.id.timeline_item_tv_WATCHED_DROPPED)
        val watchedDroppedIcon: ImageView = view.findViewById(R.id.icon_watched_dropped_drawable)
        val timelineLineTop: ImageView = view.findViewById(R.id.timeline_line_top)
        val timelineLineBottom: ImageView = view.findViewById(R.id.timeline_line_bottom)


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