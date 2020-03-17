package amichealpalmer.kotlin.filmfocus.fragments

import amichealpalmer.kotlin.filmfocus.data.TimelineItem
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

private const val ARG_TIMELINE_LIST = "timelineList"

enum class TIMELINE_ITEM_CONTEXT_ACTION_TYPE{
    TIMELINE_ITEM_REMOVE
}

class HistoryFragment: Fragment() { // note code duplication with other fragments

    private val TAG = "HistoryFragment"

    internal var callback: OnTimelineItemSelectedListener? = null
    private lateinit var timelineList: ArrayList<TimelineItem>
    lateinit var recyclerView: RecyclerView

    fun setOnTimelineItemSelectedListener(callback: OnTimelineItemSelectedListener){
        this.callback = callback
    }

    interface OnTimelineItemSelectedListener{
        fun onTimelineItemSelected(item: TimelineItem, type: TIMELINE_ITEM_CONTEXT_ACTION_TYPE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (arguments != null){
            timelineList = arguments!!.getParcelableArrayList<TimelineItem>(ARG_TIMELINE_LIST) as ArrayList<TimelineItem>
        }
        //setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }



}