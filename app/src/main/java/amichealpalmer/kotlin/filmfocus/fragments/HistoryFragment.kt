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

private const val ARG_TIMELINE_LIST = "timelineList"

enum class TIMELINE_ITEM_CONTEXT_ACTION_TYPE {
    TIMELINE_ITEM_REMOVE
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
        if (arguments != null) {
            timelineList = arguments!!.getParcelableArrayList<TimelineItem>(ARG_TIMELINE_LIST) as ArrayList<TimelineItem>
        } else {
            Log.d(TAG, ".onCreate: arguments null")
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
            R.id.history_timeline_item_context_menu_remove -> true // todo: remove the item from the timeline - should display an 'are you sure?' prompt
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