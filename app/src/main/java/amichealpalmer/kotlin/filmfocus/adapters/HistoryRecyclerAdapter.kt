package amichealpalmer.kotlin.filmfocus.adapters


import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.model.FILM_RATING_VALUE
import amichealpalmer.kotlin.filmfocus.model.TIMELINE_ITEM_STATUS
import amichealpalmer.kotlin.filmfocus.model.TimelineItem
import amichealpalmer.kotlin.filmfocus.view.HistoryFragmentDirections
import android.content.Context
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.joda.time.LocalDate


class HistoryRecyclerAdapter(
        private val context: Context,
        private var timelineList: ArrayList<TimelineItem>, // List of items on the timeline
        private val navController: NavController
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

    fun replaceList(timelineList: ArrayList<TimelineItem>) {
        this.timelineList = timelineList
        notifyDataSetChanged()
    }

    fun clearList() {
        timelineList.clear()
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: HelperViewHolder, position: Int) {
        Log.d(TAG, ".onBindViewHolder begins for position $position")
        if (timelineList.size > 0) {
            Picasso.get().load(timelineList[position].film.posterURL).error(R.drawable.ic_image_loading_darkgreen_48dp)
                    .placeholder(R.drawable.ic_image_loading_darkgreen_48dp).into(holder.poster)

            val date = timelineList[position].date
            val monthProperty: LocalDate.Property = date.monthOfYear()
            // Set the views
            val dayInt = date.dayOfMonth
            var dateDay = "null"

            // Figure out the ordinal indicator
            val j = dayInt % 10
            val k = dayInt % 100
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
            Log.d(TAG, ".onBindViewHolder: configuring rating bar")
            if (timelineList[position].rating.state == FILM_RATING_VALUE.NO_RATING || timelineList[position].rating.value == 0f) {
                holder.ratingBar.visibility = View.GONE // Hide the rating bar if a rating hasn't been set
                // We also change the constraints on the review so there isn't a weird gap
                Log.d(TAG, ".onBindViewHolder: rating constraintset")
                val constraintSet = ConstraintSet()
                constraintSet.clone(holder.constraintLayoutWrapper)
                constraintSet.connect(holder.reviewTextView.id, ConstraintSet.TOP, R.id.timeline_item_poster_holder_cardview, ConstraintSet.TOP, 0)
                constraintSet.setVerticalBias(R.id.timeline_item_review_tv, 0.50f)

                constraintSet.applyTo(holder.constraintLayoutWrapper)
            } else {
                holder.ratingBar.rating = timelineList[position].rating.value
                holder.ratingBar.visibility = View.VISIBLE
            }

            Log.d(TAG, ".onBindViewHolder: configuring review visibility")
            if (timelineList[position].hasReview() && timelineList[position].getReview().isNotEmpty()) {
                val review = "\"" + timelineList[position].getReview() + "\""
                holder.reviewTextView.text = review
                holder.reviewTextView.visibility = View.VISIBLE
            } else {
                holder.reviewTextView.visibility = View.GONE // Hide review field if not set
                if (timelineList[position].rating.value > 0f) {
                    Log.d(TAG, ".onBindViewHolder: rating for $position is > 0f. review constraintset to change layout")
                    val constraintSet = ConstraintSet()
                    constraintSet.clone(holder.constraintLayoutWrapper)
                    constraintSet.setVerticalBias(R.id.timeline_item_ratingBar, 0.50f)
                    constraintSet.applyTo(holder.constraintLayoutWrapper)
                }
            }

            // Programmatically remove portion of line if this is the first or last entry in the list
            Log.d(TAG, ".onBindViewHolder: about to remove portion of line possibly. Position is: $position")
            if (timelineList.size == 1) { // Special case
                holder.timelineLineTop.visibility = View.INVISIBLE
                holder.timelineLineBottom.visibility = View.INVISIBLE
            } else {
                if (position == 0) {
                    Log.d(TAG, ".onBindViewHolder: position is 0, hiding top part of time line line")
                    holder.timelineLineTop.visibility = View.INVISIBLE
                } else if (position == (timelineList.size) - 1) {
                    Log.d(TAG, ".onBindViewHolder: position is last in array. hiding bottom part of line")
                    holder.timelineLineBottom.visibility = View.INVISIBLE
                }
            }

            // Programmatically change views if film is marked as watched or dropped
            when (timelineList[position].status) {
                TIMELINE_ITEM_STATUS.WATCHED -> {
                    holder.watchedDroppedTextView.setText(R.string.watched)
                    holder.watchedDroppedIcon.setImageResource(R.drawable.ic_watched_darkgreen_24dp)
                }
                TIMELINE_ITEM_STATUS.DROPPED -> {
                    holder.watchedDroppedIcon.setImageResource(R.drawable.ic_dropped_darkgreen_24dp)
                    holder.watchedDroppedTextView.setText(R.string.dropped)
                }
            }

            holder.poster.setOnLongClickListener {
                this.position = (holder.adapterPosition)
                false
            }
        }
        Log.d(TAG, ".onBindViewHolder ends for position $position")
    }

    override fun onViewRecycled(holder: HelperViewHolder) {
        Log.d(TAG, ".onViewRecycled called")
        holder.poster.setOnLongClickListener(null)
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
        // Defining fields we access later from outside the holder
        val poster: ImageView = view.findViewById(R.id.timeline_item_film_poster)
        val constraintLayoutWrapper: ConstraintLayout = view.findViewById(R.id.history_timeline_item_constraintLayout)
        val ratingBar: RatingBar = view.findViewById(R.id.timeline_item_ratingBar)
        val dateDayTextView: TextView = view.findViewById(R.id.timeline_item_date_day)
        val dateMonthTextView: TextView = view.findViewById(R.id.timeline_item_date_month)
        val watchedDroppedTextView: TextView = view.findViewById(R.id.timeline_item_tv_WATCHED_DROPPED)
        val watchedDroppedIcon: ImageView = view.findViewById(R.id.icon_watched_dropped_drawable)
        val timelineLineTop: ImageView = view.findViewById(R.id.timeline_line_top)
        val timelineLineBottom: ImageView = view.findViewById(R.id.timeline_line_bottom)
        val reviewTextView: TextView = view.findViewById(R.id.timeline_item_review_tv)

        init {
            poster.setOnClickListener {
                // Display FilmDetailsFragment
//                val manager = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
//                manager.setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                manager.addToBackStack(null)
//                manager.replace(R.id.activity_nav_host_fragment, FilmDetailFragment.newInstance(timelineList[adapterPosition].film.imdbID)).commit()
                val direction = HistoryFragmentDirections.actionNavHistoryFragmentToFilmDetailFragment(timelineList[adapterPosition].film.imdbID)
                navController.navigate(direction)
                // todo: this is destroying the history fragment. could crash, or otherwise it is just unwanted and resource heavy.
            }
            poster.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(menu: ContextMenu?, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
            val inflater = MenuInflater(context)
            inflater.inflate(R.menu.history_timeline_item_context_menu, menu)
        }


    }

}