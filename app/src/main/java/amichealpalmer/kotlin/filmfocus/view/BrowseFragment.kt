package amichealpalmer.kotlin.filmfocus.view

import amichealpalmer.kotlin.filmfocus.MainActivity
import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.model.entity.TIMELINE_ITEM_STATUS
import amichealpalmer.kotlin.filmfocus.model.entity.TimelineItem
import amichealpalmer.kotlin.filmfocus.model.entity.WatchlistItem
import amichealpalmer.kotlin.filmfocus.util.hideKeyboard
import amichealpalmer.kotlin.filmfocus.util.observeOnce
import amichealpalmer.kotlin.filmfocus.view.adapter.BrowseRecyclerAdapter
import amichealpalmer.kotlin.filmfocus.view.dialog.WatchedDialogFragment
import amichealpalmer.kotlin.filmfocus.viewmodel.BrowseViewModel
import amichealpalmer.kotlin.filmfocus.viewmodel.BrowseViewModelFactory
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_browse.*
import java.util.*

class BrowseFragment : Fragment(), FilmActionListener, WatchedDialogFragment.onWatchedDialogSubmissionListener {

    private lateinit var browseViewModel: BrowseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get our ViewModel
        browseViewModel = ViewModelProvider(requireActivity(), BrowseViewModelFactory(requireActivity().application))
                .get(BrowseViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_browse, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the empty view as visible by default, turn it off once a query is entered // todo: move this to a method
        if (browseViewModel.getResults().value!!.size > 0) {
            fragment_browse_empty_container.visibility = View.GONE
            fragment_browse_recycler_framelayout.visibility = View.VISIBLE
        } else {
            if (browseViewModel.getQuery().value == null) {
                fragment_browse_empty_container.visibility = View.VISIBLE
                fragment_browse_recycler_framelayout.visibility = View.GONE
            } else {
                fragment_browse_empty_container.visibility = View.GONE
                val searchView = SearchView((context as MainActivity).supportActionBar?.themedContext
                        ?: context)
                searchView.setQuery(browseViewModel.getQuery().value, false) // Set the search field to query, if it exists
            }
        }
        

        requireActivity().title = "Browse"
        setHasOptionsMenu(true)

        val recyclerView: RecyclerView = view.findViewById(R.id.browse_films_recyclerview_id)
        recyclerView.setHasFixedSize(true)
        
        // Restore scroll position (if it exists in the bundle)
        //val scrollPosition = savedInstanceState?.getInt(0) ?: 0
        //recyclerView.post { browse_films_recyclerview_id.scrollToPosition(scrollPosition) } // todo // reimplement scroll position save on config change
        
        val adapter = BrowseRecyclerAdapter()
        adapter.setFilmActionListener(this)
        recyclerView.adapter = adapter

        browseViewModel.getResults().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
            onResultsStateChange()
            browse_fragment_progressBar.visibility = View.GONE
        })

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when {
                    !recyclerView.canScrollVertically(1) -> {
                        // Request next page from repo
                        browseViewModel.nextPage()
                        browse_fragment_progressBar.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) { // Called when i.e. screen orientation changes
        super.onSaveInstanceState(outState)

        var scrollPos: Int? = null
        if (browse_films_recyclerview_id?.adapter != null) {
            //val adapter = browse_films_recyclerview_id?.adapter as BrowseRecyclerAdapter
            //val scrollPos = adapter.getAdapterPosition
            // todo: get the adapter(?) position and save it in bundle - do we need to do this?
        }
        // outState.putInt(BUNDLE_SCROLL_POSITION, scrollPos ?: 0)
    }

    // Reattaching listener interface to dialogs if they exist
    override fun onResume() {
        super.onResume()
        val watchedDialogFragment = childFragmentManager.findFragmentByTag(WatchedDialogFragment.TAG)
        if (watchedDialogFragment is WatchedDialogFragment) {
            watchedDialogFragment.setOnWatchedDialogSubmissionListener(this)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.browse_fragment_menu, menu)

        // Set up search bar
        val searchView = SearchView((context as MainActivity).supportActionBar?.themedContext
                ?: context)
        searchView.isIconifiedByDefault = false
        searchView.requestFocus()
        menu.findItem(R.id.browse_fragment_search).apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItem.SHOW_AS_ACTION_IF_ROOM)
            actionView = searchView
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val searchString = query.toLowerCase(Locale.US).trim()
                this@BrowseFragment.hideKeyboard()

                // Set up the UI
                fragment_browse_empty_container.visibility = View.GONE
                fragment_browse_recycler_framelayout.visibility = View.VISIBLE
                browse_fragment_progressBar.visibility = View.VISIBLE

                // Notify ViewModel new query has been entered, get first page
                browseViewModel.newQuery(searchString)
                browseViewModel.nextPage()

                return true
            }

            override fun onQueryTextChange(newText: String): Boolean { // Unused in this context
                return true
            }

        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun showFilmDetails(film: FilmThumbnail) {
        val fragment = FilmDetailDialogFragment.newInstance(film.imdbID)
        fragment.show(childFragmentManager, FilmDetailDialogFragment.TAG)
    }

    override fun addFilmToWatchlist(film: FilmThumbnail) {
        val currentWatchlist = browseViewModel.getWatchlist()
        // Wait for thread to get the object, and then try to add the film to the watchlist, first checking if it exists
        currentWatchlist.observeOnce(viewLifecycleOwner, androidx.lifecycle.Observer {
            var exists = false
            for (f in currentWatchlist.value!!) {
                if (f.imdbID == film.imdbID) {
                    exists = true
                    Toast.makeText(requireContext(), "${film.title} is already in Watchlist", Toast.LENGTH_SHORT).show()
                }
            }
            if (!exists) {
                browseViewModel.addToWatchlist(film)
                Toast.makeText(requireContext(), "Added ${film.title} to Watchlist", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun markFilmWatched(film: FilmThumbnail) {
        // Show the Watched dialog, which will call .onWatchedDialogSubmissionListener when submitted
        val dialogFragment = WatchedDialogFragment.newInstance(film)
        dialogFragment.setOnWatchedDialogSubmissionListener(this)
        dialogFragment.show(childFragmentManager, WatchedDialogFragment.TAG)
    }

    override fun onWatchedDialogSubmissionListener(timelineItem: TimelineItem) {
        val status = when (timelineItem.status) {
            TIMELINE_ITEM_STATUS.DROPPED -> "Dropped"
            TIMELINE_ITEM_STATUS.WATCHED -> "Watched"
        }
        Toast.makeText(requireContext(), "Marked ${timelineItem.film.title} as $status", Toast.LENGTH_SHORT).show()
        browseViewModel.markWatched(timelineItem)
    }

    override fun removeFilmFromWatchlist(watchlistItem: WatchlistItem) {
        // Does nothing in this context
    }

    // Check what view we should be displaying
    private fun onResultsStateChange() {
        if (!browseViewModel.getResults().value!!.isNullOrEmpty()) {
            fragment_browse_empty_container.visibility = View.GONE
            fragment_browse_recycler_framelayout.visibility = View.VISIBLE
        } else {
            fragment_browse_empty_container.visibility = View.VISIBLE
            fragment_browse_recycler_framelayout.visibility = View.GONE
        }
    }

    companion object {
        private const val TAG = "BrowseFragment"
        //private const val BUNDLE_SCROLL_POSITION = "scrollPosition"

    }

}
