package amichealpalmer.kotlin.filmfocus.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.data.Film
import amichealpalmer.kotlin.filmfocus.data.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.helpers.FilmSearch
import android.util.Log
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_film_details.*
import kotlinx.android.synthetic.main.fragment_film_details.view.*

private const val ARG_PARAM1 = "imdbID"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FilmDetailsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FilmDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class FilmDetailsFragment : Fragment() {

    private var listener: OnFragmentInteractionListener? = null
    private lateinit var film: Film
    private lateinit var imdbID: String

    private val TAG = "FilmDetailsFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, ".OnCreate called")
        if (arguments != null) {
            Log.d(TAG, "arguments non-null, retrieving film/show details")
            imdbID = arguments!!.getString(ARG_PARAM1) as String
        } else {
            Log.d(TAG, ".onCreate: arguments null")
        }

        // Use id to set film object
        FilmSearch(this).getFilmByID(imdbID)

        super.onCreate(savedInstanceState)
    }

    fun onFilmInfoDownload(film: Film) { // Sub-optimal?
        Log.d(TAG, ".onFilmInfoDownload called")
        this.film = film
        fragment_film_details_tv_title.text = film.title
        fragment_film_details_tv_director.text = film.director
        fragment_film_details_tv_year.text = film.year
        fragment_film_details_tv_runtime.text = film.runtime
        fragment_film_details_tv_plot.text = film.plot
        fragment_film_details_tv_awards.text = film.awards
        fragment_film_details_tv_cast.text = film.actors
        fragment_film_details_tv_genre.text = film.genre
        fragment_film_details_tv_imdbScore.text = film.imdbRating
        fragment_film_details_tv_metacriticScore.text = film.metascore
        fragment_film_details_tv_language.text = film.language

        Log.d(TAG, "Picasso: setting poster url for the layout")
        Picasso.get().load(film.posterURL).error(R.drawable.placeholder_imageloading)
                .placeholder(R.drawable.placeholder_imageloading).into(fragment_film_details_iv_poster)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        Log.d(TAG, ".onCreateView called")


        // While the ASyncTask runs, we display the view devoid of information. Perhaps not the most user friendly experience? Might want to display a single loading icon
        val view = inflater.inflate(R.layout.fragment_film_details, container, false)
        Log.d(TAG, "Setting text fields for the layout")

        view.fragment_film_details_tv_title.text = "..."
        view.fragment_film_details_tv_director.text = "..."
        view.fragment_film_details_tv_year.text = "..."
        view.fragment_film_details_tv_runtime.text = "..."
        view.fragment_film_details_tv_plot.text = "..."
        view.fragment_film_details_tv_awards.text = "..."
        view.fragment_film_details_tv_cast.text = "..."
        view.fragment_film_details_tv_genre.text = "..."
        view.fragment_film_details_tv_imdbScore.text = "..."
        view.fragment_film_details_tv_metacriticScore.text = "..."
        view.fragment_film_details_tv_language.text = "..."

        //  Log.d(TAG, "Picasso: setting poster url for the layout")
        Picasso.get().load(R.drawable.placeholder_imageloading).error(R.drawable.placeholder_imageloading)
                .placeholder(R.drawable.placeholder_imageloading).into(view.fragment_film_details_iv_poster)

        return view
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FilmDetailsFragment.
         */

        fun newInstance(film: Film): FilmDetailsFragment {
            val fragment = FilmDetailsFragment()
            val args = Bundle()
            args.putParcelable(ARG_PARAM1, film)
            fragment.arguments = args
            return fragment
        }
    }
}
