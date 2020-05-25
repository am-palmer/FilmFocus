package amichealpalmer.kotlin.filmfocus.view

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.adapter.HistoryRecyclerAdapter
import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.model.entity.TimelineItem
import amichealpalmer.kotlin.filmfocus.model.entity.WatchlistItem
import amichealpalmer.kotlin.filmfocus.view.dialog.ConfirmClearHistoryDialogFragment
import amichealpalmer.kotlin.filmfocus.view.dialog.ConfirmRemoveFilmFromHistoryDialogFragment
import amichealpalmer.kotlin.filmfocus.view.dialog.EditHistoryItemDialogFragment
import amichealpalmer.kotlin.filmfocus.view.dialog.WatchedDialogFragment
import amichealpalmer.kotlin.filmfocus.viewmodel.TimelineViewModel
import amichealpalmer.kotlin.filmfocus.viewmodel.TimelineViewModelFactory
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_history.*

// todo: continue refactoring to use viewmodel
// todo: sometimes the literal time 'line' breaks when a film is removed.

class HistoryFragment : Fragment(), FilmActionListener, HistoryRecyclerAdapter.TimelineActionListener, ConfirmRemoveFilmFromHistoryDialogFragment.OnConfirmRemoveFilmDialogActionListener, EditHistoryItemDialogFragment.onHistoryEditDialogSubmissionListener, ConfirmClearHistoryDialogFragment.onConfirmClearHistoryDialogListener { // note code duplication with other fragments

    // todo: check we can still edit items properly, and have multiple copies of films -> room replacement strategy?

    private lateinit var timelineViewModel: TimelineViewModel
    private lateinit var adapter: HistoryRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timelineViewModel = ViewModelProvider(requireActivity(), TimelineViewModelFactory(requireActivity().application))
                .get(TimelineViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = "History"
        //onTimelineItemListStateChange()
        setHasOptionsMenu(true)

        // Adapter
        val recyclerView: RecyclerView = view.findViewById(R.id.fragment_history_timeline_rv)
        recyclerView.setHasFixedSize(true)
        val adapter = HistoryRecyclerAdapter()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter.setFilmActionListener(this)
        adapter.setTimelineActionListener(this)
        recyclerView.adapter = adapter
        this.adapter = adapter

        // Observer
        timelineViewModel.getTimelineItemList().observe(viewLifecycleOwner, Observer {
            //Log.d(TAG, ".observe starts: list size is " + timelineViewModel.getTimelineItemList().value?.size)
            adapter.submitList(it.reversed())
            adapter.notifyDataSetChanged()
            onTimelineItemListStateChange()
        })

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.history_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.history_fragment_moreMenu_clearHistory -> {
                val fragment = ConfirmClearHistoryDialogFragment.newInstance(this)
                fragment.show(childFragmentManager, ConfirmClearHistoryDialogFragment.TAG)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Reattach listener interfaces to dialog fragments associated with this fragment
    override fun onResume() {
        super.onResume()
        val confirmClearHistoryDialogFragment = childFragmentManager.findFragmentByTag(ConfirmClearHistoryDialogFragment.TAG)
        if (confirmClearHistoryDialogFragment is ConfirmClearHistoryDialogFragment) {
            confirmClearHistoryDialogFragment.setOnConfirmClearHistoryDialogListener(this)
        }
        val editHistoryItemDialogFragment = childFragmentManager.findFragmentByTag(EditHistoryItemDialogFragment.TAG)
        if (editHistoryItemDialogFragment is EditHistoryItemDialogFragment) {
            editHistoryItemDialogFragment.setHistoryEditDialogSubmissionListener(this)
        }
        val confirmRemoveFilmFromHistoryDialogFragment = childFragmentManager.findFragmentByTag(ConfirmRemoveFilmFromHistoryDialogFragment.TAG)
        if (confirmRemoveFilmFromHistoryDialogFragment is ConfirmRemoveFilmFromHistoryDialogFragment) {
            confirmRemoveFilmFromHistoryDialogFragment.setOnConfirmRemoveFilmDialogActionListener(this)
        }
    }

    /*
//    override fun onContextItemSelected(item: MenuItem): Boolean {
//        Log.d(TAG, ".onContextItemSelected begins")
//        val adapter = recyclerView.adapter as HistoryRecyclerAdapter
//        var position = -1
//        try {
//            position = adapter.position
//            Log.d(TAG, "onContextItemSelected: position is $position")
//        } catch (e: NullPointerException) {
//            Log.e(TAG, e.localizedMessage, e)
//            return super.onContextItemSelected(item)
//        }
//        when (item.itemId) {
//            R.id.history_timeline_item_context_menu_remove -> {
//                val timelineItem = adapter.getItem(position)
//                val dialogFragment = ConfirmRemoveFilmFromHistoryDialogFragment.newInstance(timelineItem)
//                dialogFragment.setOnConfirmRemoveFilmDialogActionListener(this)
//                dialogFragment.show(childFragmentManager, ConfirmRemoveFilmFromHistoryDialogFragment.TAG)
//            }
//            R.id.history_timeline_item_context_menu_addToWatchlist -> {
//                val timelineItem = adapter.getItem(position)
//                when (addItemToWatchlist(timelineItem.film)) { // Note secondary effect of call
//                    true -> Toast.makeText(requireContext(), "Added ${timelineItem.film.title} to Watchlist", Toast.LENGTH_SHORT).show()
//                    false -> Toast.makeText(requireContext(), "${timelineItem.film.title} is already in Watchlist", Toast.LENGTH_SHORT).show()
//                }
//            }
//            R.id.history_timeline_item_context_menu_editReview -> {
//                val timelineItem = adapter.getItem(position)
//                val editFragment = EditHistoryItemDialogFragment.newInstance(timelineItem, position)
//                editFragment.setHistoryEditDialogSubmissionListener(this)
//                editFragment.show(childFragmentManager, EditHistoryItemDialogFragment.TAG)
//            }
//        }
//        return super.onContextItemSelected(item)
//    }

     */

    override fun onConfirmRemoveItemDialogAction(timelineItem: TimelineItem) {
        removeTimelineItem(timelineItem)
        Toast.makeText(requireContext(), "Removed ${timelineItem.film.title} from History", Toast.LENGTH_SHORT).show()
    }

    override fun addFilmToWatchlist(film: FilmThumbnail) {
        timelineViewModel.addItemToWatchlist(film)
    }

    override fun markFilmWatched(film: FilmThumbnail) {
        // Does nothing here
    }

    override fun removeFilmFromWatchlist(watchlistItem: WatchlistItem) {
        // todo: ability to remove film from watchlist from other fragments
    }

    override fun showFilmDetails(film: FilmThumbnail) {
        val fragment = FilmDetailDialogFragment.newInstance(film.imdbID)
        fragment.show(childFragmentManager, FilmDetailDialogFragment.TAG)
    }

    override fun editTimelineItem(item: TimelineItem, position: Int) {
        val dialogFragment = EditHistoryItemDialogFragment.newInstance(item, position)
        dialogFragment.setHistoryEditDialogSubmissionListener(this)
        dialogFragment.show(childFragmentManager, WatchedDialogFragment.TAG)
    }

    override fun onEditHistoryItemDialogSubmissionListener(timelineItem: TimelineItem, arrayPosition: Int) {
        timelineViewModel.addUpdateItem(timelineItem)
        adapter.notifyItemChanged(arrayPosition)
        adapter.notifyDataSetChanged()
        Toast.makeText(requireContext(), "Updated details for ${timelineItem.film.title}", Toast.LENGTH_SHORT).show()
    }

    override fun removeTimelineItem(item: TimelineItem) {
        // todo: show confirm dialog
        timelineViewModel.removeItem(item)
    }

    override fun onConfirmClearHistoryDialogSubmit() {
        clearHistory()
    }

    private fun clearHistory() {
        timelineViewModel.clearTimeline()
        Toast.makeText(context, "Cleared History", Toast.LENGTH_SHORT).show()
    }

    // Called when we need to check if we should display the empty view for the Timeline fragment
    private fun onTimelineItemListStateChange() {
        if (!timelineViewModel.getTimelineItemList().value.isNullOrEmpty()) {
            fragment_history_empty_view_container.visibility = View.GONE
            fragment_history_timeline_rv.visibility = View.VISIBLE
        } else {
            fragment_history_empty_view_container.visibility = View.VISIBLE
            fragment_history_timeline_rv.visibility = View.GONE
        }
    }

    companion object {
        private const val TAG = "HistoryFragment"
    }

}

