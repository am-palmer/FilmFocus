package amichealpalmer.kotlin.filmfocus.view

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.databinding.FragmentHistoryBinding
import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.model.entity.TimelineItem
import amichealpalmer.kotlin.filmfocus.util.InjectorUtils
import amichealpalmer.kotlin.filmfocus.view.adapter.HistoryRecyclerAdapter
import amichealpalmer.kotlin.filmfocus.view.dialog.EditHistoryItemDialogFragment
import amichealpalmer.kotlin.filmfocus.view.dialog.WatchedDialogFragment
import amichealpalmer.kotlin.filmfocus.view.listener.HistoryActionListener
import amichealpalmer.kotlin.filmfocus.viewmodel.TimelineViewModel
import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HistoryFragment : Fragment(), HistoryActionListener, HistoryRecyclerAdapter.TimelineActionListener, EditHistoryItemDialogFragment.OnHistoryEditDialogSubmissionListener {

    private var recyclerView: RecyclerView? = null
    private lateinit var binding: FragmentHistoryBinding

    private val timelineViewModel: TimelineViewModel by viewModels {
        InjectorUtils.provideTimelineViewModelFactory(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = "History"
        setHasOptionsMenu(true)

        // Adapter
        recyclerView = view.findViewById(R.id.fragment_history_timeline_rv)
        recyclerView?.setHasFixedSize(true)
        val adapter = HistoryRecyclerAdapter()
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        adapter.setFilmActionListener(this)
        adapter.setTimelineActionListener(this)
        binding.fragmentHistoryTimelineRv.adapter = adapter

        subscribeUi(adapter, binding)
    }

    private fun subscribeUi(adapter: HistoryRecyclerAdapter, binding: FragmentHistoryBinding) {
        // Observer
        timelineViewModel.getTimelineItemList().observe(viewLifecycleOwner, Observer {
            adapter.submitList(it.reversed())
            binding.hasItems = !it.isNullOrEmpty()
        })
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

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerView = null
    }

    override fun addFilmToWatchlist(film: FilmThumbnail) {
        timelineViewModel.addItemToWatchlist(film)
    }

    override fun showFilmDetails(film: FilmThumbnail) {
        val fragment = FilmDetailDialogFragment.newInstance(film.imdbID)
        fragment.show(childFragmentManager, FilmDetailDialogFragment.TAG)
    }

    override fun editTimelineItem(adapter: HistoryRecyclerAdapter, item: TimelineItem, position: Int) {
        val dialogFragment = EditHistoryItemDialogFragment.newInstance(adapter, item, position)
        dialogFragment.setHistoryEditDialogSubmissionListener(this)
        dialogFragment.show(childFragmentManager, WatchedDialogFragment.TAG)
    }

    override fun onEditHistoryItemDialogSubmissionListener(adapter: HistoryRecyclerAdapter, timelineItem: TimelineItem, arrayPosition: Int) {
        timelineViewModel.addUpdateItem(timelineItem)
        adapter.notifyItemChanged(arrayPosition)
        adapter.notifyDataSetChanged()
        Toast.makeText(requireContext(), "Updated details for ${timelineItem.film.title}", Toast.LENGTH_SHORT).show()
    }

    override fun removeTimelineItem(adapter: HistoryRecyclerAdapter, item: TimelineItem, position: Int) {
        AlertDialog.Builder(requireContext())
                .setTitle(R.string.remove_item_from_history)
                .setMessage(R.string.dialog_confirmRemovalFromHistory_prompt)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    timelineViewModel.removeItem(item)
                    adapter.notifyItemRemoved(position)
                }
                .setNegativeButton(android.R.string.no, null).show()
    }

    private fun clearHistory() {
        timelineViewModel.clearTimeline()
    }

}

