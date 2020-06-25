package amichealpalmer.kotlin.filmfocus.view

import amichealpalmer.kotlin.filmfocus.MainActivity
import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.databinding.FragmentBrowseBinding
import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.model.entity.TIMELINE_ITEM_STATUS
import amichealpalmer.kotlin.filmfocus.model.entity.TimelineItem
import amichealpalmer.kotlin.filmfocus.model.entity.WatchlistItem
import amichealpalmer.kotlin.filmfocus.util.InjectorUtils
import amichealpalmer.kotlin.filmfocus.util.hideKeyboard
import amichealpalmer.kotlin.filmfocus.util.observeOnce
import amichealpalmer.kotlin.filmfocus.view.adapter.BrowseRecyclerAdapter
import amichealpalmer.kotlin.filmfocus.view.dialog.WatchedDialogFragment
import amichealpalmer.kotlin.filmfocus.viewmodel.BrowseViewModel
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_browse.*
import java.util.*

class BrowseFragment : Fragment(), FilmActionListener, WatchedDialogFragment.onWatchedDialogSubmissionListener {

    private var recyclerView: RecyclerView? = null
    private var searchView: SearchView? = null
    private lateinit var binding: FragmentBrowseBinding

    private val browseViewModel: BrowseViewModel by viewModels {
        InjectorUtils.provideBrowseViewModelFactory(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentBrowseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = "Browse"
        setHasOptionsMenu(true)

        val adapter = BrowseRecyclerAdapter()
        adapter.setFilmActionListener(this)
        recyclerView = view.findViewById(R.id.browse_films_recyclerview_id)
        recyclerView?.setHasFixedSize(true)
        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.browseFilmsRecyclerviewId.adapter = adapter
        subscribeUi(adapter, binding)

        recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when {
                    !recyclerView.canScrollVertically(1) -> {
                        if (browseViewModel.getHaveMoreResults().value == true && !browseViewModel.getCurrentlyLoadingResults()) {
                            Log.d(TAG, "recyclerview: can't scroll vertically down, and not currently loading results: loading more results")
                            // Request next page from repo
                            browseViewModel.nextPage()
                            browse_fragment_progressBar.visibility = View.VISIBLE
                        }
                    }
                }
            }
        })

        //val sharedPrefs = requireActivity().getSharedPreferences(BROWSE_SHAREDPREF_KEY, Context.MODE_PRIVATE)
        // todo: none of this works - values are saved, but restoring doesn't work
        //recyclerView?.post { recyclerViewscrollToPosition(sharedPrefs.getInt(SHAREDPREF_SCROLL_POSITION, 0)) }

    }

    private fun subscribeUi(adapter: BrowseRecyclerAdapter, binding: FragmentBrowseBinding) {
        browseViewModel.getResults().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
            binding.hasResults = !it.isNullOrEmpty()
            browse_fragment_progressBar.visibility = View.GONE
        })

        // Changes to false when we reach the bottom of the result list. Hide the progress bar to indicate there are no more results. Could display a toast message
        browseViewModel.getHaveMoreResults().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            browse_fragment_progressBar.visibility = View.GONE
        })

    }

    // Reattaching listener interface to dialogs if they exist
    override fun onResume() {
        super.onResume()
        val watchedDialogFragment = childFragmentManager.findFragmentByTag(WatchedDialogFragment.TAG)
        if (watchedDialogFragment is WatchedDialogFragment) {
            watchedDialogFragment.setOnWatchedDialogSubmissionListener(this)
        }
        //searchView?.setQuery(sharedPrefs.getString(SHAREDPREF_QUERY, ""), false)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        recyclerView = null // Preventing memory leak
        searchView = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.browse_fragment_menu, menu)

        // Set up search bar
        searchView = SearchView((context as MainActivity).supportActionBar?.themedContext
                ?: context)
        searchView?.isIconifiedByDefault = false
        searchView?.requestFocus()
        menu.findItem(R.id.browse_fragment_search).apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItem.SHOW_AS_ACTION_IF_ROOM)
            actionView = searchView
        }

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
        // Does nothing in this context -> TODO: Expand functionality so films can be deleted from the watchlist from the browse/history view
        // Films that are in the watchlist should have an indicator in the browse/history views
    }

    companion object {
        private const val TAG = "BrowseFragment"
        private const val SHAREDPREF_SCROLL_POSITION = "scrollPosition"
        private const val SHAREDPREF_QUERY = "query"
        private const val BROWSE_SHAREDPREF_KEY = "filmFocusBrowseSharedPreferences"

    }

}
