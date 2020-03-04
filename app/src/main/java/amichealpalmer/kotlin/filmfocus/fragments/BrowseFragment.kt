package amichealpalmer.kotlin.filmfocus.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.adapters.BrowseRecyclerAdapter
import amichealpalmer.kotlin.filmfocus.data.FilmThumbnail
import android.content.Context
import android.util.Log
import android.view.MenuItem
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val ARG_RESULTS = "resultList"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [BrowseFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [BrowseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BrowseFragment : Fragment() {

    private var listener: OnFragmentInteractionListener? = null
    private lateinit var resultList: ArrayList<FilmThumbnail>
    lateinit var recyclerView: RecyclerView
    private val TAG = "BrowseFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, ".onCreate called")
        if (arguments != null) {
            Log.d(TAG, ".onCreateView: arguments != null. setting resultList var")
            resultList = arguments!!.getParcelableArrayList<FilmThumbnail>(ARG_RESULTS) as ArrayList<FilmThumbnail>
            Log.d(TAG, "resultList is: ${resultList}")
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
        recyclerView.adapter = BrowseRecyclerAdapter(activity!!, resultList)
        return view
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return super.onContextItemSelected(item)
    }

    // todo: implement onAttach
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        if (context is WatchlistFragment.OnFragmentInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
//        }
//    }


    companion object {

        fun newInstance(resultList: ArrayList<FilmThumbnail>): BrowseFragment {
            val fragment = BrowseFragment()
            val args = Bundle()
            args.putParcelableArrayList(ARG_RESULTS, resultList)
            fragment.arguments = args
            return fragment
        }

    }


    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

}
