package amichealpalmer.kotlin.filmfocus.view

import amichealpalmer.kotlin.filmfocus.MainActivity
import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.adapter.BrowseRecyclerAdapter
import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.model.entity.TimelineItem
import amichealpalmer.kotlin.filmfocus.view.dialog.WatchedDialogFragment
import amichealpalmer.kotlin.filmfocus.viewmodel.FilmThumbnailViewModel
import amichealpalmer.kotlin.filmfocus.viewmodel.FilmThumbnailViewModelFactory
import android.app.Activity
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_browse.*
import java.util.*


private const val ARG_RESULTS = "resultList"
private const val ARG_SEARCH_STRING = "searchString"


// todo: no longer retains content upon switching fragment in menu

class BrowseFragment : Fragment(), FilmActionListener, WatchedDialogFragment.onWatchedDialogSubmissionListener {

    private lateinit var resultListViewModel: FilmThumbnailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get our ViewModel
        resultListViewModel = ViewModelProvider(requireActivity(), FilmThumbnailViewModelFactory(requireActivity().application))
                .get(FilmThumbnailViewModel::class.java)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_browse, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the empty view as visible by default, turn it off once a query is entered
        if (resultListViewModel.getResults().value!!.size > 0) {
            fragment_search_empty_container.visibility = View.GONE
            fragment_browse_recycler_framelayout.visibility = View.VISIBLE
        } else {
            if (resultListViewModel.getQuery().value == null) {
                fragment_search_empty_container.visibility = View.VISIBLE
                fragment_browse_recycler_framelayout.visibility = View.GONE
            } else {
                fragment_search_empty_container.visibility = View.GONE
                val searchView = SearchView((context as MainActivity).supportActionBar?.themedContext
                        ?: context)
                searchView.setQuery(resultListViewModel.getQuery().value, false) // Set the search field to query, if it exists
            }
        }

        val scrollPosition = savedInstanceState?.getInt(BUNDLE_SCROLL_POSITION) ?: 0

        requireActivity().title = "Browse"
        setHasOptionsMenu(true)
        //activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) todo: needed?

        val recyclerView: RecyclerView = view.findViewById(R.id.browse_films_recyclerview_id)
        recyclerView.setHasFixedSize(true)
        recyclerView.post { browse_films_recyclerview_id.scrollToPosition(scrollPosition) }
        val adapter = BrowseRecyclerAdapter()
        recyclerView.adapter = adapter

        resultListViewModel.getResults().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            //Log.d(TAG, "change in observable!")
            adapter.submitList(it)
            adapter.notifyDataSetChanged() // todo: we shouldn't need to call this directly, fix
            browse_fragment_progressBar.visibility = View.GONE // todo: check this does what we expect
            if (it.isNotEmpty()){
                fragment_search_empty_container.visibility = View.GONE
                fragment_browse_recycler_framelayout.visibility = View.VISIBLE
            }
        })

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when {
                    !recyclerView.canScrollVertically(1) -> {
                        // Request next page from repo
                        resultListViewModel.nextPage()
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
            val adapter = browse_films_recyclerview_id?.adapter as BrowseRecyclerAdapter
            //scrollPos = adapter.getAdapterPosition
            // todo: get the adapter position and save it in bundle
        }
        outState.putInt(BUNDLE_SCROLL_POSITION, scrollPos ?: 0)
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

                // Close the keyboard
                val inputMethodManager: InputMethodManager = activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(activity!!.currentFocus!!.windowToken, 0)

                // Set up the UI
                fragment_search_empty_container.visibility = View.GONE
                fragment_browse_recycler_framelayout.visibility = View.VISIBLE
                browse_fragment_progressBar.visibility = View.VISIBLE

                // Notify ViewModel new query has been entered, get first page
                resultListViewModel.newQuery(searchString)
                resultListViewModel.nextPage()

                return true
            }

            override fun onQueryTextChange(newText: String): Boolean { // Unused in this context
                return true
            }

        })
        searchView.setOnClickListener { view -> }

        super.onCreateOptionsMenu(menu, inflater)

    }


    // todo: now in adapter, code the listeners here
//
//    override fun onContextItemSelected(item: MenuItem): Boolean { // todo: code duplication with watchlistRecyclerAdapter
//        try {
//            Log.d(TAG, ".onContextItemSelected called")
//            Log.d(TAG, "menu item: ${item}")
//            val adapter = recyclerView?.adapter as BrowseRecyclerAdapter
//            var position: Int
//            try {
//                position = adapter.position
//            } catch (e: NullPointerException) {
//                Log.d(TAG, e.localizedMessage, e)
//                return super.onContextItemSelected(item)
//            }
//
//            when (item.itemId) {
//                R.id.browse_film_context_menu_add -> {
//                    val film = adapter.getFilmThumbnailAtPosition(position)
//                    when (addFilmToWatchlist(film)) { // Note secondary effect
//                        true -> Toast.makeText(requireContext(), "Added ${film.title} to Watchlist", Toast.LENGTH_SHORT).show()
//                        false -> Toast.makeText(requireContext(), "${film.title} is already in Watchlist", Toast.LENGTH_SHORT).show()
//                    }
//                }
//                R.id.browse_film_context_menu_mark_watched -> {
//                    val film = adapter.getFilmThumbnailAtPosition(position)
//                    val dialogFragment = WatchedDialogFragment.newInstance(film)
//                    dialogFragment.setOnWatchedDialogSubmissionListener(this)
//                    dialogFragment.show(childFragmentManager, WatchedDialogFragment.TAG)
//                }
//                else -> true
//            }
//
//            return super.onContextItemSelected(item)
//
//        } catch (e: NullPointerException) {
//            Log.e(TAG, ".onContextItemSelected: NPE - callback null?")
//        }
//        return false
//    }

    override fun onWatchedDialogSubmissionListener(timelineItem: TimelineItem) {
        // markFilmAsWatched(timelineItem)
        // todo: notify repository, update timeline
    }

    override fun markFilmWatched(film: FilmThumbnail) {
        TODO("Not yet implemented")
    }

    // todo: if a film is in the watchlist, the option to remove it should be shown in the context menu regardless of where the context menu is shown
    override fun removeFilmFromWatchlist(film: FilmThumbnail) {
        TODO("Not yet implemented")
    }

    override fun addFilmToWatchlist(film: FilmThumbnail) {
        TODO("Not yet implemented")
    }

    override fun showFilmDetails(film: FilmThumbnail) {
        TODO("Not yet implemented")
    }

    companion object {
        private const val TAG = "BrowseFragment"
        private const val BUNDLE_SCROLL_POSITION = "scrollPosition"

        fun newInstance(searchString: String?): BrowseFragment {
            val fragment = BrowseFragment()
            val args = Bundle()
            if (searchString != null) {
                args.putString(ARG_SEARCH_STRING, searchString)
            }
            fragment.arguments = args
            return fragment
        }

    }

}
