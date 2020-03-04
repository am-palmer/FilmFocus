package amichealpalmer.kotlin.filmfocus.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.adapters.BrowseRecyclerAdapter
import amichealpalmer.kotlin.filmfocus.data.FilmThumbnail
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val ARG_LIST = "watchlist"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [WatchlistFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [WatchlistFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

enum class FILM_CONTEXT_ACTION_TYPE{
    WATCHLIST_REMOVE, WATCHLIST_MARK_WATCHED
}

class WatchlistFragment : Fragment() { // note: code duplication with browsefragment. possibly have browsefragment and searchfragment/watchlistfragment subclasses

    //private var listener: OnFragmentInteractionListener? = null
    private val TAG = "WatchlistFragment"
    internal var callback: onFilmSelectedListener? = null
    private lateinit var watchlist: ArrayList<FilmThumbnail>
    lateinit var recyclerView: RecyclerView

    fun setOnFilmSelectedListener(callback: onFilmSelectedListener) {
        this.callback = callback
    }

    interface onFilmSelectedListener {
        fun onFilmSelected(position: Int, type: FILM_CONTEXT_ACTION_TYPE)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, ".onCreate called")
        if (arguments != null) {
            Log.d(TAG, ".onCreateView: arguments != null. setting resultList var")
            watchlist = arguments!!.getParcelableArrayList<FilmThumbnail>(ARG_LIST) as ArrayList<FilmThumbnail>
        } else {
            Log.d(TAG, ".onCreateView: arguments is null")
        }
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        Log.d(TAG, ".onCreateView called")
        var view = inflater.inflate(R.layout.fragment_browse, container, false)
        recyclerView = view.findViewById<RecyclerView>(R.id.browse_films_recyclerview_id)
        recyclerView.layoutManager = GridLayoutManager(activity, 3)
        recyclerView.adapter = BrowseRecyclerAdapter(activity!!, watchlist)
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is onFilmSelectedListener) {
            callback = context
        } else {
            throw RuntimeException(context.toString() + " must implement onFilmSelectedListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, ".onContextItemSelected called")
        Log.d(TAG, "item: ${item}")
        val adapter = recyclerView!!.adapter as BrowseRecyclerAdapter
        var position = -1
        try {
            position = adapter.position
        } catch (e: java.lang.Exception) { // too general
            Log.d(TAG, e.localizedMessage, e)
            return super.onContextItemSelected(item)
        }
        when (item.itemId) {
            R.id.film_thumbnail_context_menu_option1 -> true //Toast.makeText(this, "Option 1", Toast.LENGTH_SHORT).show()
            R.id.film_thumbnail_context_menu_option2 -> {

                //watchlistHelper().removeFilmFromWatchlist(adapter.getItem(position))
                //Toast.makeText(this, "Removed", Toast.LENGTH_SHORT).show()
            }
            else -> true
        }

        return super.onContextItemSelected(item)
    }

    companion object {
        //val ARG_PARAM = "resultList"

        fun newInstance(resultList: ArrayList<FilmThumbnail>): WatchlistFragment {
            val fragment = WatchlistFragment()
            val args = Bundle()
            args.putParcelableArrayList(ARG_LIST, resultList)
            fragment.arguments = args
            return fragment
        }
    }
}
