package amichealpalmer.kotlin.filmfocus.view.adapter

import amichealpalmer.kotlin.filmfocus.databinding.HistoryListItemBinding
import amichealpalmer.kotlin.filmfocus.model.entity.TIMELINE_ITEM_STATUS
import amichealpalmer.kotlin.filmfocus.model.entity.TimelineItem
import amichealpalmer.kotlin.filmfocus.view.FilmActionListener
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

//todo: data binding https://androidwave.com/android-data-binding-recyclerview/

class HistoryRecyclerAdapter : ListAdapter<TimelineItem, HistoryRecyclerAdapter.TimelineItemViewHolder>(DIFF_CALLBACK) {

    private var listener: FilmActionListener? = null
    private var timelineListener: TimelineActionListener? = null

    fun setFilmActionListener(listener: FilmActionListener) {
        this.listener = listener
    }

    fun setTimelineActionListener(timelineListener: TimelineActionListener) {
        this.timelineListener = timelineListener
    }

    interface TimelineActionListener {
        fun editTimelineItem(adapter: HistoryRecyclerAdapter, item: TimelineItem, position: Int)
        fun removeTimelineItem(adapter: HistoryRecyclerAdapter, item: TimelineItem, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineItemViewHolder {
        return TimelineItemViewHolder(HistoryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: TimelineItemViewHolder, position: Int) {
        val currentItem: TimelineItem = getItem(position)
        holder.bind(currentItem)
    }

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TimelineItem>() {
            override fun areItemsTheSame(oldItem: TimelineItem, newItem: TimelineItem): Boolean {
                return areContentsTheSame(oldItem, newItem)
            }

            override fun areContentsTheSame(oldItem: TimelineItem, newItem: TimelineItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class TimelineItemViewHolder(private val binding: HistoryListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TimelineItem){
            binding.apply {
                timelineItem = item
                isWatched = when (item.status){
                    TIMELINE_ITEM_STATUS.Watched -> true
                    TIMELINE_ITEM_STATUS.Dropped -> false
                }
                timelineItemFilmPoster.setOnClickListener {
                    listener?.showFilmDetails(item.film)
                }

                executePendingBindings()
            }
        }


//        private val poster: ImageView = view.findViewById(R.id.timeline_item_film_poster)
//        private val constraintLayoutWrapper: ConstraintLayout = view.findViewById(R.id.history_timeline_item_constraintLayout)
//        private val ratingBar: RatingBar = view.findViewById(R.id.timeline_item_ratingBar)
//        private val dateDayTextView: TextView = view.findViewById(R.id.timeline_item_date_day)
//        private val dateMonthTextView: TextView = view.findViewById(R.id.timeline_item_date_month)
//        private val watchedDroppedTextView: TextView = view.findViewById(R.id.timeline_item_tv_WATCHED_DROPPED)
//        private val watchedDroppedIcon: ImageView = view.findViewById(R.id.icon_watched_dropped_drawable)
//        private val timelineLineTop: ImageView = view.findViewById(R.id.timeline_line_top)
//        private val timelineLineBottom: ImageView = view.findViewById(R.id.timeline_line_bottom)
//        private val reviewTextView: TextView = view.findViewById(R.id.timeline_item_review_tv)

//        fun setViewsForHolder() { // todo: data binding?
//            val position = adapterPosition
//            if (position != RecyclerView.NO_POSITION) {
//                val item = getItem(position)
//
//                poster.setOnClickListener {
//                    // Display FilmDetailsDialogFragment
//                    listener?.showFilmDetails(item.film)
//                }
//
//                poster.setOnCreateContextMenuListener { menu, v, menuInfo ->
//                    menu?.add(R.string.edit)?.setOnMenuItemClickListener {
//                        timelineListener?.editTimelineItem(this@HistoryRecyclerAdapter, item, position)
//                        true
//                    }
//                    menu?.add(R.string.add_to_watchlist)?.setOnMenuItemClickListener {
//                        listener?.addFilmToWatchlist(item.film)
//                        true
//                    }
//                    menu?.add(R.string.remove_item_from_history)?.setOnMenuItemClickListener {
//                        timelineListener?.removeTimelineItem(this@HistoryRecyclerAdapter, item, position)
//                        true
//                    }
//                }
//
//                // Set the views
//                dateMonthTextView.text = item.date.monthOfYear().asText
//                dateDayTextView.text = getDayWithOrdinalIndicator(item.date.dayOfMonth)
//
//                if (item.rating == 0f) { // Hide the rating bar if a rating hasn't been set or it is 0
//                    ratingBar.visibility = View.GONE
//
//                    // Change the constraints on the review to prevent gaps
//                    val constraintSet = ConstraintSet()
//                    constraintSet.clone(constraintLayoutWrapper)
//                    constraintSet.connect(reviewTextView.id, ConstraintSet.TOP, R.id.timeline_item_poster_holder_cardview, ConstraintSet.TOP, 0)
//                    constraintSet.setVerticalBias(R.id.timeline_item_review_tv, 0.50f)
//                    constraintSet.applyTo(constraintLayoutWrapper)
//
//                } else { // If the rating is not 0, set the value
//                    ratingBar.rating = item.rating
//                    ratingBar.visibility = View.VISIBLE
//                }
//
//                if (item.hasReview() && item.getReview().isNotEmpty()) {
//                    // Enclose the review in quotation marks
//                    val review = "\"" + item.getReview() + "\""
//
//                    reviewTextView.text = review
//                    reviewTextView.visibility = View.VISIBLE
//                } else { // Hide review field if not set
//                    reviewTextView.visibility = View.GONE
//                    if (item.rating > 0f) { // Set constraints in case that there is a rating but no review
//                        val constraintSet = ConstraintSet()
//                        constraintSet.clone(constraintLayoutWrapper)
//                        constraintSet.setVerticalBias(R.id.timeline_item_ratingBar, 0.50f)
//                        constraintSet.applyTo(constraintLayoutWrapper)
//                    }
//                }
//
//                // Programmatically change views if film is marked as watched or dropped
//                when (item.status) {
//                    TIMELINE_ITEM_STATUS.Watched -> {
//                        watchedDroppedTextView.setText(R.string.watched)
//                        watchedDroppedIcon.setImageResource(R.drawable.ic_watched_darkgreen_24dp)
//                    }
//                    TIMELINE_ITEM_STATUS.Dropped -> {
//                        watchedDroppedIcon.setImageResource(R.drawable.ic_dropped_darkgreen_24dp)
//                        watchedDroppedTextView.setText(R.string.dropped)
//                    }
//                }
//
//            }
//        }
//
//        // Controls the actual 'line' shown in the timeline
//        // todo use data binding
//        fun setLineSegments() {
//            val position = adapterPosition
//            //Log.d(TAG, "setLineSegements: adapterPosition is $position")
//            if (position != RecyclerView.NO_POSITION) {
//                // Programmatically remove portion(s) of timeline if this is the first or last entry in the list
//                if (position == 0 && position == itemCount - 1) {
//                    timelineLineTop.visibility = View.INVISIBLE
//                    timelineLineBottom.visibility = View.INVISIBLE
//                } else {
//                    when (position) {
//                        0 -> {
//                            timelineLineTop.visibility = View.INVISIBLE
//                        }
//                        itemCount - 1 -> {
//                            timelineLineBottom.visibility = View.INVISIBLE
//                        }
//                        else -> {
//                            timelineLineTop.visibility = View.VISIBLE
//                            timelineLineBottom.visibility = View.VISIBLE
//                        }
//                    }
//                }
//            }
//        }
//
//        // Figure out the ordinal indicator to display for the date and append it, i.e. 4 becomes 4th
//        private fun getDayWithOrdinalIndicator(day: Int): String {
//            val j = day % 10
//            val k = day % 100
//            return when {
//                j == 1 && k != 11 -> {
//                    day.toString() + "st"
//                }
//                j == 2 && k != 12 -> {
//                    day.toString() + "nd"
//                }
//                j == 3 && k != 13 -> {
//                    day.toString() + "rd"
//                }
//                else -> {
//                    day.toString() + "th"
//                }
//            }
//        }


    }

}