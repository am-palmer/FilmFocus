package amichealpalmer.kotlin.filmfocus.view.adapter

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.databinding.HistoryListItemBinding
import amichealpalmer.kotlin.filmfocus.model.entity.TIMELINE_ITEM_STATUS
import amichealpalmer.kotlin.filmfocus.model.entity.TimelineItem
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class HistoryRecyclerAdapter : ListAdapter<TimelineItem, HistoryRecyclerAdapter.TimelineItemViewHolder>(DIFF_CALLBACK) {

    private var listener: amichealpalmer.kotlin.filmfocus.view.listener.HistoryActionListener? = null
    private var historyListener: HistoryActionListener? = null

    fun setFilmActionListener(listener: amichealpalmer.kotlin.filmfocus.view.listener.HistoryActionListener) {
        this.listener = listener
    }

    fun setHistoryActionListener(historyListener: HistoryActionListener) {
        this.historyListener = historyListener
    }

    interface HistoryActionListener {
        fun editTimelineItem(adapter: HistoryRecyclerAdapter, item: TimelineItem, position: Int)
        fun removeTimelineItem(adapter: HistoryRecyclerAdapter, item: TimelineItem, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineItemViewHolder {
        return TimelineItemViewHolder(HistoryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: TimelineItemViewHolder, position: Int) {
        val currentItem: TimelineItem = getItem(position)
        holder.bind(currentItem, position)
    }

    companion object {

        private const val TAG = "HistoryRecyclerAdapter"

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

        fun bind(item: TimelineItem, position: Int) {
            binding.apply {
                // Set binding values specified in the xml
                timelineItem = item
                isWatched = when (item.status) {
                    TIMELINE_ITEM_STATUS.Watched -> true
                    TIMELINE_ITEM_STATUS.Dropped -> false
                }


                // Todo: if user removes an item these won't be updated. Must reexecute / update bindings
                // Bind values for timeline segments
                isFirst = when (position) {
                    0 -> true
                    else -> false
                }
                isLast = position == itemCount - 1

                // Setup tap listener
                timelineItemFilmPoster.setOnClickListener {
                    listener?.showFilmDetails(item.film)
                }

                // Context menu for options
                timelineItemFilmPoster.setOnCreateContextMenuListener { menu, _, _ ->
                    menu?.add(R.string.edit)?.setOnMenuItemClickListener {
                        historyListener?.editTimelineItem(this@HistoryRecyclerAdapter, item, position)
                        true
                    }
                    menu?.add(R.string.add_to_watchlist)?.setOnMenuItemClickListener {
                        listener?.addFilmToWatchlist(item.film)
                        true
                    }
                    menu?.add(R.string.remove_item_from_history)?.setOnMenuItemClickListener {
                        historyListener?.removeTimelineItem(this@HistoryRecyclerAdapter, item, position)
                        true
                    }
                }

                executePendingBindings()
            }
        }

    }

}