package amichealpalmer.kotlin.filmfocus.view

import amichealpalmer.kotlin.filmfocus.MainActivity
import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.databinding.FragmentWatchlistBinding
import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.model.entity.TIMELINE_ITEM_STATUS
import amichealpalmer.kotlin.filmfocus.model.entity.TimelineItem
import amichealpalmer.kotlin.filmfocus.model.entity.WatchlistItem
import amichealpalmer.kotlin.filmfocus.util.InjectorUtils
import amichealpalmer.kotlin.filmfocus.util.hideKeyboard
import amichealpalmer.kotlin.filmfocus.view.adapter.WatchlistRecyclerAdapter
import amichealpalmer.kotlin.filmfocus.view.dialog.WatchedDialogFragment
import amichealpalmer.kotlin.filmfocus.viewmodel.WatchlistViewModel
import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView

class WatchlistFragment : Fragment(), FilmActionListener, WatchedDialogFragment.onWatchedDialogSubmissionListener {

    private var recyclerView: RecyclerView? = null
    private var adapter: WatchlistRecyclerAdapter? = null
    private var searchView: SearchView? = null
    private var query: String? = null
    private lateinit var binding: FragmentWatchlistBinding

    private val watchlistViewModel: WatchlistViewModel by viewModels {
        InjectorUtils.provideWatchlistViewModelFactory(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentWatchlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = "Watchlist"
        setHasOptionsMenu(true)

        // Set up adapter
        recyclerView = view.findViewById(R.id.watchlist_recyclerview)
        recyclerView?.setHasFixedSize(true)
        adapter = WatchlistRecyclerAdapter()
        adapter?.setFilmActionListener(this)
        binding.watchlistRecyclerview.adapter = adapter
        subscribeUi(adapter!!, binding)
    }

    private fun subscribeUi(adapter: WatchlistRecyclerAdapter, binding: FragmentWatchlistBinding) {
        //Register observer for View model
        watchlistViewModel.getWatchlist().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            adapter.modifyList(it) // Use modifyList rather than submitList for purposes of filtering
            binding.hasWatchlistItems = !it.isNullOrEmpty()
        })

    }

    override fun onResume() {
        super.onResume()
        // Reattaching listener
        val watchedDialogFragment = childFragmentManager.findFragmentByTag(WatchedDialogFragment.TAG)
        if (watchedDialogFragment is WatchedDialogFragment) { // May be null
            watchedDialogFragment.setOnWatchedDialogSubmissionListener(this)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.watchlist_fragment_menu, menu)

        searchView = SearchView((context as MainActivity).supportActionBar?.themedContext
                ?: context)
        searchView?.isIconifiedByDefault = false
        searchView?.requestFocus()
        menu.findItem(R.id.watchlist_fragment_search).apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItem.SHOW_AS_ACTION_IF_ROOM)
            actionView = searchView
            // Check if a query exists (i.e. after orientation change), if so expand search view
            if (query != null) {
                expandActionView()
            }
        }

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                this@WatchlistFragment.hideKeyboard()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // We use the adapter filter to update the RecyclerView
                adapter?.filter(newText)
                return true
            }
        })

        searchView?.post { searchView?.setQuery(query, true) }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (!searchView?.query.isNullOrEmpty()) {
            outState.putString(BUNDLE_QUERY, searchView?.query.toString())
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            query = savedInstanceState.getString(BUNDLE_QUERY)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerView = null
        adapter = null
        searchView = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.watchlist_fragment_more_menu_removeAll -> {
                // Display an alert dialog to confirm this action with the user before taking it
                AlertDialog.Builder(requireContext())
                        .setTitle(R.string.remove_all_from_watchlist)
                        .setMessage(R.string.dialog_clear_watchlist_prompt)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes) { _, _ ->
                            clearWatchlist()
                        }
                        .setNegativeButton(android.R.string.no, null).show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun markFilmWatched(film: FilmThumbnail) {
        // Show the "Watched" dialog
        val dialogFragment = WatchedDialogFragment.newInstance(film)
        dialogFragment.setOnWatchedDialogSubmissionListener(this)
        dialogFragment.show(childFragmentManager, WatchedDialogFragment.TAG)
    }

    // Called when user submits Watched dialog
    override fun onWatchedDialogSubmissionListener(timelineItem: TimelineItem) {
        var status = when (timelineItem.status) {
            TIMELINE_ITEM_STATUS.DROPPED -> "Dropped"
            TIMELINE_ITEM_STATUS.WATCHED -> "Watched"
        }
        Toast.makeText(requireContext(), "Marked ${timelineItem.film.title} as $status", Toast.LENGTH_SHORT).show()
        addFilmToHistory(timelineItem)
    }

    override fun showFilmDetails(film: FilmThumbnail) {
        val fragment = FilmDetailDialogFragment.newInstance(film.imdbID)
        fragment.show(childFragmentManager, FilmDetailDialogFragment.TAG)
    }

    override fun removeFilmFromWatchlist(watchlistItem: WatchlistItem) {
        watchlistViewModel.removeItem(watchlistItem)
    }

    private fun clearWatchlist() {
            watchlistViewModel.clearWatchlist()
    }

    private fun addFilmToHistory(timelineItem: TimelineItem) {
        watchlistViewModel.addItemToHistory(timelineItem)
        removeFilmFromWatchlist(timelineItem.film as WatchlistItem)
    }

    override fun addFilmToWatchlist(film: FilmThumbnail) {
        // Has no function here
        // Todo: rewrite so this isn't here
    }

    companion object {
        private const val BUNDLE_QUERY = "bundle_query"

    }

}

