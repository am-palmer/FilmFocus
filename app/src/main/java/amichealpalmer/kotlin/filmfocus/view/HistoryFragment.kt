package amichealpalmer.kotlin.filmfocus.view

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.model.entity.TimelineItem
import amichealpalmer.kotlin.filmfocus.model.entity.WatchlistItem
import amichealpalmer.kotlin.filmfocus.view.adapter.HistoryRecyclerAdapter
import amichealpalmer.kotlin.filmfocus.view.dialog.EditHistoryItemDialogFragment
import amichealpalmer.kotlin.filmfocus.view.dialog.WatchedDialogFragment
import amichealpalmer.kotlin.filmfocus.viewmodel.TimelineViewModel
import amichealpalmer.kotlin.filmfocus.viewmodel.TimelineViewModelFactory
import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_history.*

// todo: sometimes the literal time 'line' breaks when a film is removed.

class HistoryFragment : Fragment(), FilmActionListener, HistoryRecyclerAdapter.TimelineActionListener, EditHistoryItemDialogFragment.onHistoryEditDialogSubmissionListener {

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
            adapter.submitList(it.reversed())
            onTimelineItemListStateChange()
        })

        //adapter.notifyDataSetChanged() todo: may be needed
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.history_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.history_fragment_moreMenu_clearHistory -> {
                AlertDialog.Builder(requireContext())
                        .setTitle(R.string.history_menu_clear_history)
                        .setMessage(R.string.dialog_clear_history_prompt)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes) { _, _ ->
                            clearHistory()
                        }
                        .setNegativeButton(android.R.string.no, null).show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Reattach listener interfaces to dialog fragments associated with this fragment
    override fun onResume() {
        super.onResume()
        val editHistoryItemDialogFragment = childFragmentManager.findFragmentByTag(EditHistoryItemDialogFragment.TAG)
        if (editHistoryItemDialogFragment is EditHistoryItemDialogFragment) {
            editHistoryItemDialogFragment.setHistoryEditDialogSubmissionListener(this)
        }
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
        adapter.notifyItemChanged(arrayPosition) // todo: calls onbindviewholder, which calls viewholder inner class, but the changes aren't reflected immediately?
        adapter.notifyDataSetChanged()
        Toast.makeText(requireContext(), "Updated details for ${timelineItem.film.title}", Toast.LENGTH_SHORT).show()
    }

    override fun removeTimelineItem(item: TimelineItem, position: Int) {
        AlertDialog.Builder(requireContext())
                .setTitle(R.string.remove_item_from_history)
                .setMessage(R.string.dialog_confirmRemovalFromHistory_prompt)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    adapter.notifyItemRemoved(position)
                    // Update the items before and after the removed item (if they exist)
                    if (adapter.currentList[position + 1] != null) {
                        adapter.notifyItemChanged(position + 1)
                    }
                    if (adapter.currentList[position - 1] != null) {
                        adapter.notifyItemChanged(position - 1)
                    }
                    adapter.notifyDataSetChanged()
                    timelineViewModel.removeItem(item)
                }
                .setNegativeButton(android.R.string.no, null).show()
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

