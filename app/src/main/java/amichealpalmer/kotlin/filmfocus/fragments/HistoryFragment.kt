package amichealpalmer.kotlin.filmfocus.fragments

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.adapters.HistoryRecyclerAdapter
import amichealpalmer.kotlin.filmfocus.data.TimelineItem
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_dialog_generic_confirm.*
import java.lang.NullPointerException

private const val ARG_TIMELINE_LIST = "timelineList"

enum class TIMELINE_ITEM_CONTEXT_ACTION_TYPE {
    TIMELINE_ITEM_REMOVE, TIMELINE_ADD_TO_WATCHLIST, TIMELINE_ITEM_UPDATE
}

class HistoryFragment : Fragment(), ConfirmRemoveFilmFromHistoryDialogFragment.OnConfirmRemoveFilmDialogActionListener, EditHistoryItemDialogFragment.onHistoryEditDialogSubmissionListener { // note code duplication with other fragments

    private val TAG = "HistoryFragment"

    private var callback: OnTimelineItemSelectedListener? = null
    private lateinit var timelineList: ArrayList<TimelineItem>
    lateinit var recyclerView: RecyclerView

    fun setOnTimelineItemSelectedListener(callback: OnTimelineItemSelectedListener) {
        this.callback = callback
    }

    interface OnTimelineItemSelectedListener {
        fun onTimelineItemSelected(item: TimelineItem, type: TIMELINE_ITEM_CONTEXT_ACTION_TYPE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, ".onCreate begins")
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
        Log.d(TAG, ".onCreate: timelineList created. list has ${timelineList.size} items")
        //setHasOptionsMenu(true) ?
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        Log.d(TAG, ".onCreateView begins")
        var view = inflater.inflate(R.layout.fragment_history, container, false)
        recyclerView = view.findViewById<RecyclerView>(R.id.fragment_history_timeline_rv)
        //recyclerView.layoutManager = GridLayoutManager(activity, 3)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = HistoryRecyclerAdapter(activity!!, timelineList)
        return view
    }

    override fun onAttach(context: Context) {
        Log.d(TAG, ".onAttach begins")
        super.onAttach(context)
        if (context is OnTimelineItemSelectedListener) {
            callback = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnTimelineItemSelectedListener")
        }
    }

    override fun onDetach() {
        Log.d(TAG, ".onDetach begins")
        super.onDetach()
        callback = null
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, ".onContextItemSelected begins")
        val adapter = recyclerView.adapter as HistoryRecyclerAdapter
        var position = -1
        try {
            position = adapter.position
            Log.d(TAG, "onContextItemSelected: position is $position")
        } catch (e: NullPointerException) {
            Log.d(TAG, e.localizedMessage, e)
            return super.onContextItemSelected(item)
        }
        when (item.itemId) {
            R.id.history_timeline_item_context_menu_remove -> {
                val timelineItem = adapter.getItem(position)
                val dialogFragment = ConfirmRemoveFilmFromHistoryDialogFragment.newInstance(timelineItem)
                dialogFragment.setOnConfirmRemoveFilmDialogActionListener(this)
                dialogFragment.show(fragmentManager!!, "fragment_confirm_remove_dialog")
            }
            R.id.history_timeline_item_context_menu_addToWatchlist -> {
                val timelineItem = adapter.getItem(position)
                callback?.onTimelineItemSelected(timelineItem, TIMELINE_ITEM_CONTEXT_ACTION_TYPE.TIMELINE_ADD_TO_WATCHLIST)
            }
            R.id.history_timeline_item_context_menu_editReview -> {
                val timelineItem = adapter.getItem(position)
                val editFragment = EditHistoryItemDialogFragment.newInstance(timelineItem, position)
                editFragment.setHistoryEditDialogSubmissionListener(this)
                editFragment.show(fragmentManager!!, "fragment_edit_history_item_dialog")
            }
            else -> true
        }

        return super.onContextItemSelected(item)
    }

    override fun onConfirmRemoveFilmDialogAction(timelineItem: TimelineItem) {
        //val timelineItem = adapter.getItem(position)
        val adapter = recyclerView.adapter as HistoryRecyclerAdapter
        timelineList.remove(timelineItem)
        adapter.removeTimelineItem(timelineItem)
        adapter.notifyDataSetChanged()
        // Call listener so stored data can be updated
        callback!!.onTimelineItemSelected(timelineItem, TIMELINE_ITEM_CONTEXT_ACTION_TYPE.TIMELINE_ITEM_REMOVE)
    }

    companion object {
        private val TAG = "HistoryFragmentCompan"
        fun newInstance(timelineList: ArrayList<TimelineItem>): HistoryFragment {
            Log.d(TAG, ".newInstance")
            val fragment = HistoryFragment()
            val args = Bundle()
            args.putParcelableArrayList(ARG_TIMELINE_LIST, timelineList)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onEditHistoryItemDialogSubmissionListener(timelineItem: TimelineItem, arrayPosition: Int) {
        val adapter = recyclerView.adapter as HistoryRecyclerAdapter
        timelineList[arrayPosition] = timelineItem
        adapter.notifyItemChanged(arrayPosition) // Is this enough?
        callback!!.onTimelineItemSelected(timelineItem, TIMELINE_ITEM_CONTEXT_ACTION_TYPE.TIMELINE_ITEM_UPDATE)
    }

}

class ConfirmRemoveFilmFromHistoryDialogFragment : DialogFragment(), View.OnClickListener {

    private val TAG = "ConfirmRemoveFilmHisDia"

    private lateinit var callback: OnConfirmRemoveFilmDialogActionListener

    private lateinit var timelineItem: TimelineItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            timelineItem = arguments!!.getParcelable<TimelineItem>("timelineItem") as TimelineItem
        } catch (e: NullPointerException) {
            Log.wtf(TAG, ".onCreate - failed to retrieve timelineItem")
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        isCancelable = true
        return inflater.inflate(R.layout.fragment_dialog_generic_confirm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prompt = "Are you sure you want to remove ${timelineItem.film.title} from your history?"
        fragment_dialog_generic_cancelButton.setOnClickListener(this)
        fragment_dialog_generic_takeActionButton.setOnClickListener(this)
        fragment_dialog_generic_prompt_text.text = prompt
        fragment_dialog_generic_takeActionButton.setText(R.string.remove)

        dialog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            fragment_dialog_generic_cancelButton.id -> this.dismiss()
            fragment_dialog_generic_takeActionButton.id -> { // We remove the item from the history
                callback.onConfirmRemoveFilmDialogAction(timelineItem)
                this.dismiss()
            }
        }
    }

    interface OnConfirmRemoveFilmDialogActionListener {
        fun onConfirmRemoveFilmDialogAction(timelineItem: TimelineItem)
    }

    fun setOnConfirmRemoveFilmDialogActionListener(callback: OnConfirmRemoveFilmDialogActionListener) {
        this.callback = callback
    }

    companion object {

        fun newInstance(timelineItem: TimelineItem): ConfirmRemoveFilmFromHistoryDialogFragment {
            val fragment = ConfirmRemoveFilmFromHistoryDialogFragment()
            val bundle = Bundle()
            bundle.putParcelable("timelineItem", timelineItem)
            fragment.arguments = bundle
            return fragment
        }

    }

}