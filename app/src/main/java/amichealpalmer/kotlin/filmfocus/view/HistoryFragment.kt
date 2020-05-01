package amichealpalmer.kotlin.filmfocus.view

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.adapters.HistoryRecyclerAdapter
import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.model.TimelineItem
import amichealpalmer.kotlin.filmfocus.view.dialog.ConfirmClearHistoryDialogFragment
import amichealpalmer.kotlin.filmfocus.view.dialog.ConfirmRemoveFilmFromHistoryDialogFragment
import amichealpalmer.kotlin.filmfocus.view.dialog.EditHistoryItemDialogFragment
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_history.*

private const val ARG_TIMELINE_LIST = "timelineList"

// todo: sometimes the literal time 'line' breaks when a film is removed.

class HistoryFragment : Fragment(), ConfirmRemoveFilmFromHistoryDialogFragment.OnConfirmRemoveFilmDialogActionListener, EditHistoryItemDialogFragment.onHistoryEditDialogSubmissionListener, ConfirmClearHistoryDialogFragment.onConfirmClearHistoryDialogListener { // note code duplication with other fragments

    private val TAG = "HistoryFragment"
    private var callback: OnTimelineItemSelectedListener? = null
    private lateinit var timelineList: ArrayList<TimelineItem>
    private var recyclerView: RecyclerView? = null

    fun setOnTimelineItemSelectedListener(callback: OnTimelineItemSelectedListener) {
        this.callback = callback
    }

    interface OnTimelineItemSelectedListener {
        fun addFilmToWatchlistFromHistory(film: FilmThumbnail): Boolean // Todo: would be nice if the browse and history fragments could make use of the same method in activity
        fun clearHistory(): Boolean
        fun removeItemFromHistory(timelineItem: TimelineItem)
        fun updateHistoryItem(timelineItem: TimelineItem)
        fun retrieveHistory(): ArrayList<TimelineItem>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, ".onCreate begins")
        // Get list from SharedPrefs
        timelineList = callback!!.retrieveHistory()
        // Reverse the list so it's shown from newest to oldest
        timelineList.reverse()
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        Log.d(TAG, ".onCreateView begins")

        val view = inflater.inflate(R.layout.fragment_history, container, false)
        recyclerView = view.findViewById(R.id.fragment_history_timeline_rv)
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        recyclerView?.adapter = HistoryRecyclerAdapter(requireActivity(), timelineList, findNavController())
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        onTimelineItemListStateChange()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.history_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.history_fragment_moreMenu_clearHistory -> {
                val fragment = ConfirmClearHistoryDialogFragment.newInstance(this)
                fragment.show(requireFragmentManager(), "fragment_confirm_clear_history_dialog")
                return true
            }
        }

        return super.onOptionsItemSelected(item)
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

    // Reattach listener interfaces to dialog fragments associated with this fragment
    override fun onResume() {
        super.onResume()
        val confirmClearHistoryDialogFragment = parentFragmentManager.findFragmentByTag(ConfirmClearHistoryDialogFragment.TAG)
        if (confirmClearHistoryDialogFragment is ConfirmClearHistoryDialogFragment) {
            confirmClearHistoryDialogFragment.setOnConfirmClearHistoryDialogListener(this)
        }
        val editHistoryItemDialogFragment = parentFragmentManager.findFragmentByTag(EditHistoryItemDialogFragment.TAG)
        if (editHistoryItemDialogFragment is EditHistoryItemDialogFragment) {
            editHistoryItemDialogFragment.setHistoryEditDialogSubmissionListener(this)
        }
        val confirmRemoveFilmFromHistoryDialogFragment = parentFragmentManager.findFragmentByTag(ConfirmRemoveFilmFromHistoryDialogFragment.TAG)
        if (confirmRemoveFilmFromHistoryDialogFragment is ConfirmRemoveFilmFromHistoryDialogFragment) {
            confirmRemoveFilmFromHistoryDialogFragment.setOnConfirmRemoveFilmDialogActionListener(this)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, ".onContextItemSelected begins")
        val adapter = recyclerView?.adapter as HistoryRecyclerAdapter
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
                dialogFragment.show(requireFragmentManager(), "fragment_confirm_remove_dialog")
            }
            R.id.history_timeline_item_context_menu_addToWatchlist -> {
                val timelineItem = adapter.getItem(position)
                when (addItemToWatchlist(timelineItem.film)) { // Note secondary effect of call
                    true -> Toast.makeText(requireContext(), "Added ${timelineItem.film.title} to Watchlist", Toast.LENGTH_SHORT).show()
                    false -> Toast.makeText(requireContext(), "${timelineItem.film.title} is already in Watchlist", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.history_timeline_item_context_menu_editReview -> {
                val timelineItem = adapter.getItem(position)
                val editFragment = EditHistoryItemDialogFragment.newInstance(timelineItem, position)
                editFragment.setHistoryEditDialogSubmissionListener(this)
                editFragment.show(requireFragmentManager(), "fragment_edit_history_item_dialog")
            }
            else -> true
        }

        return super.onContextItemSelected(item)
    }

    override fun onConfirmRemoveItemDialogAction(timelineItem: TimelineItem) {
        removeItemFromTimeline(timelineItem)
        Toast.makeText(requireContext(), "Removed ${timelineItem.film.title} from History", Toast.LENGTH_SHORT).show()
    }

    override fun onConfirmClearHistoryDialogSubmit() {
        when (timelineList.isEmpty()) {
            true -> Toast.makeText(requireContext(), "History is already empty", Toast.LENGTH_SHORT).show()
            false -> {
                clearHistory()
                Toast.makeText(requireContext(), "Cleared History", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEditHistoryItemDialogSubmissionListener(timelineItem: TimelineItem, arrayPosition: Int) {
        updateTimelineItem(timelineItem, arrayPosition)
        Toast.makeText(requireContext(), "Updated details for ${timelineItem.film.title}", Toast.LENGTH_SHORT).show()
    }

    private fun removeItemFromTimeline(item: TimelineItem) {
        val adapter = recyclerView?.adapter as HistoryRecyclerAdapter
        timelineList.remove(item)
        onTimelineItemListStateChange()
        adapter.removeTimelineItem(item)
        adapter.notifyDataSetChanged()
        // Update sharedPrefs
        callback!!.removeItemFromHistory(item)
    }

    private fun updateTimelineItem(item: TimelineItem, arrayPosition: Int) {
        val adapter = recyclerView?.adapter as HistoryRecyclerAdapter
        timelineList[arrayPosition] = item
        adapter.notifyItemChanged(arrayPosition) // Is this enough?
        onTimelineItemListStateChange()
        // Update sharedPrefs
        callback!!.updateHistoryItem(item)
    }

    private fun clearHistory(): Boolean {
        if (timelineList.size > 0) {
            timelineList.clear()
            onTimelineItemListStateChange()
            val recyclerAdapter = recyclerView?.adapter as HistoryRecyclerAdapter
            recyclerAdapter.clearList()
        }
        // Update SharedPrefs
        return callback!!.clearHistory() // Passing boolean around which we don't need
    }

    private fun addItemToWatchlist(film: FilmThumbnail): Boolean {
        return callback!!.addFilmToWatchlistFromHistory(film)
    }

    // Called when we need to check if we should display the empty view for the Timeline fragment
    private fun onTimelineItemListStateChange() {
        if (timelineList.isNotEmpty()) {
            fragment_history_empty_view_container.visibility = View.GONE
            fragment_history_timeline_rv.visibility = View.VISIBLE
        } else {
            fragment_history_empty_view_container.visibility = View.VISIBLE
            fragment_history_timeline_rv.visibility = View.GONE
        }
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

}

