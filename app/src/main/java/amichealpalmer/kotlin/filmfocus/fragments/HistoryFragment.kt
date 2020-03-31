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
import kotlinx.android.synthetic.main.fragment_history_confirmremove_dialog.*
import java.lang.NullPointerException

private const val ARG_TIMELINE_LIST = "timelineList"

enum class TIMELINE_ITEM_CONTEXT_ACTION_TYPE {
    TIMELINE_ITEM_REMOVE, TIMELINE_ADD_TO_WATCHLIST
}

class HistoryFragment : Fragment(), ConfirmRemoveFilmFromHistoryDialogFragment.OnConfirmRemoveFilmDialogActionListener { // note code duplication with other fragments

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
            R.id.history_timeline_item_context_menu_addToWatchlist -> true //todo: allow adding film to watchlist from history
            else -> true
        }

        return super.onContextItemSelected(item)
    }

    override fun onConfirmRemoveFilmDialogAction(timelineItem: TimelineItem, answer: ConfirmRemoveFilmFromHistoryDialogFragment.DIALOG_OUTCOME) {
        when (answer) {
            ConfirmRemoveFilmFromHistoryDialogFragment.DIALOG_OUTCOME.NO -> true // Do nothing
            ConfirmRemoveFilmFromHistoryDialogFragment.DIALOG_OUTCOME.YES -> {
                //val timelineItem = adapter.getItem(position)
                val adapter = recyclerView.adapter as HistoryRecyclerAdapter
                timelineList.remove(timelineItem)
                adapter.removeTimelineItem(timelineItem)
                adapter.notifyDataSetChanged()
                // Call listener so stored data can be updated
                callback!!.onTimelineItemSelected(timelineItem, TIMELINE_ITEM_CONTEXT_ACTION_TYPE.TIMELINE_ITEM_REMOVE)
            }
        }
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

class ConfirmRemoveFilmFromHistoryDialogFragment : DialogFragment(), View.OnClickListener {

    private val TAG = "ConfirmRemoveFilmHisDia"

    private lateinit var callback: OnConfirmRemoveFilmDialogActionListener

    private lateinit var timelineItem: TimelineItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            timelineItem = arguments!!.getParcelable<TimelineItem>("timelineItem") as TimelineItem
        } catch (e: NullPointerException) {
            Log.e(TAG, ".onCreate - failed to retrieve timelineItem")
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        isCancelable = true
        return inflater.inflate(R.layout.fragment_history_confirmremove_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prompt = "Are you sure you want to remove ${timelineItem.film.title} from your  history?"
        fragment_confirm_remove_dialog_button_cancel.setOnClickListener(this)
        fragment_confirm_remove_dialog_button_remove.setOnClickListener(this)
        fragment_confirm_remove_dialog_textPrompt.text = prompt

        dialog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            fragment_confirm_remove_dialog_button_cancel.id -> this.dismiss()
            fragment_confirm_remove_dialog_button_remove.id -> {
                callback.onConfirmRemoveFilmDialogAction(timelineItem, DIALOG_OUTCOME.YES)
                this.dismiss()
            }
        }
    }

    enum class DIALOG_OUTCOME {
        YES, NO
    }

    interface OnConfirmRemoveFilmDialogActionListener {
        fun onConfirmRemoveFilmDialogAction(timelineItem: TimelineItem, answer: DIALOG_OUTCOME)
    }

    fun setOnConfirmRemoveFilmDialogActionListener(callback: ConfirmRemoveFilmFromHistoryDialogFragment.OnConfirmRemoveFilmDialogActionListener) {
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