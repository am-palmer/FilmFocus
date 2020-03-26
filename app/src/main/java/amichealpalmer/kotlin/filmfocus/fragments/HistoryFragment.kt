package amichealpalmer.kotlin.filmfocus.fragments

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.adapters.HistoryRecyclerAdapter
import amichealpalmer.kotlin.filmfocus.data.TimelineItem
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.lang.NullPointerException
import kotlin.reflect.typeOf

private const val ARG_TIMELINE_LIST = "timelineList"

enum class TIMELINE_ITEM_CONTEXT_ACTION_TYPE {
    TIMELINE_ITEM_REMOVE, TIMELINE_ADD_TO_WATCHLIST
}

class HistoryFragment : Fragment() { // note code duplication with other fragments

    private val TAG = "HistoryFragment"

    internal var callback: OnTimelineItemSelectedListener? = null
    private lateinit var timelineList: ArrayList<TimelineItem>
    lateinit var recyclerView: RecyclerView

    fun setOnTimelineItemSelectedListener(callback: OnTimelineItemSelectedListener) {
        this.callback = callback
    }

    interface OnTimelineItemSelectedListener {
        fun onTimelineItemSelected(item: TimelineItem, type: TIMELINE_ITEM_CONTEXT_ACTION_TYPE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        try {
            val bundleList = arguments!!.getParcelableArrayList<TimelineItem>(ARG_TIMELINE_LIST) as ArrayList<TimelineItem>
            if (!bundleList.isNullOrEmpty()) {
                timelineList = ArrayList<TimelineItem>()
                timelineList.addAll(bundleList)
                timelineList.reverse()
            } else { // Presumably no items in timeline yet
                timelineList = ArrayList<TimelineItem>()
            }
        } catch (e: NullPointerException) {
            Log.e(TAG, ".onCreate: timelineList null in arguments")
        }
        //setHasOptionsMenu(true) ?
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        Log.d(TAG, ".onCreateView called")
        var view = inflater.inflate(R.layout.fragment_history, container, false)
        recyclerView = view.findViewById<RecyclerView>(R.id.fragment_history_timeline_rv)
        //recyclerView.layoutManager = GridLayoutManager(activity, 3)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = HistoryRecyclerAdapter(activity!!, timelineList)
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnTimelineItemSelectedListener) {
            callback = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnTimelineItemSelectedListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val adapter = recyclerView.adapter as HistoryRecyclerAdapter
        var position = -1
        try {
            position = adapter.position
        } catch (e: java.lang.Exception) { // todo: too general
            Log.d(TAG, e.localizedMessage, e)
            return super.onContextItemSelected(item)
        }
        when (item.itemId) {
            R.id.history_timeline_item_context_menu_remove -> {
                // todo: this should display an 'are you sure' prompt
                val timelineItem = adapter.getItem(position)
                timelineList.remove(timelineItem)
                adapter.removeTimelineItem(timelineItem)
                adapter.notifyDataSetChanged()
                // listener call
                callback!!.onTimelineItemSelected(timelineItem, TIMELINE_ITEM_CONTEXT_ACTION_TYPE.TIMELINE_ITEM_REMOVE)
            }
            else -> true
        }

        return super.onContextItemSelected(item)
    }

    companion object {

        fun newInstance(timelineList: ArrayList<TimelineItem>): HistoryFragment {
            val fragment = HistoryFragment()
            val args = Bundle()
            args.putParcelableArrayList(ARG_TIMELINE_LIST, timelineList)
            fragment.arguments = args
            return fragment
        }
    }

}