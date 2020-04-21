package amichealpalmer.kotlin.filmfocus.view

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.model.Film
import amichealpalmer.kotlin.filmfocus.utilities.GetJSONFilm
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_film_details.*

class FilmDetailsFragment : Fragment() {

    //private lateinit var film: Film
    //private lateinit var imdbID: String
    private val ARG_IMDBID = "imdbID"
    private val TAG = "FilmDetailsFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, ".OnCreate called")
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        Log.d(TAG, ".onCreateView starts")
        val view = inflater.inflate(R.layout.fragment_film_details, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // While the ASyncTask runs, we show a ProgressBar
        film_details_progressBar?.visibility = View.VISIBLE

        // Hiding views
        fragment_film_details_tv_title?.visibility = View.GONE
        fragment_film_details_tv_director?.visibility = View.GONE
        fragment_film_details_tv_year?.visibility = View.GONE
        fragment_film_details_tv_runtime?.visibility = View.GONE
        fragment_film_details_tv_plot?.visibility = View.GONE
        fragment_film_details_tv_awards?.visibility = View.GONE
        fragment_film_details_tv_cast?.visibility = View.GONE
        fragment_film_details_tv_genre?.visibility = View.GONE
        fragment_film_details_tv_imdbScore?.visibility = View.GONE
        fragment_film_details_tv_metacriticScore?.visibility = View.GONE
        fragment_film_details_tv_language?.visibility = View.GONE
        fragment_film_details_iv_poster?.visibility = View.GONE
        fragment_film_details_tv_DIRECTEDBY?.visibility = View.GONE
        fragment_film_details_tv_METACRITIC?.visibility = View.GONE
        fragment_film_details_tv_IMDB?.visibility = View.GONE
        fragment_film_details_tv_STARRING?.visibility = View.GONE

        // Use the IMDB ID to retrieve film object todo: move this logic out of view
        val imdbID = arguments?.getString(ARG_IMDBID) as String
        GetJSONFilm(this, getString(R.string.OMDB_API_KEY)).execute(imdbID)

        super.onViewCreated(view, savedInstanceState)
    }

    // Called by GetJSONFilm to pass the film object
    fun onFilmInfoDownload(film: Film) {
        Log.d(TAG, ".onFilmInfoDownload starts")
        //this.film = film

        // Hide the ProgressBar
        film_details_progressBar?.visibility = View.GONE

        // Set view fields
        fragment_film_details_tv_title?.text = film.title
        fragment_film_details_tv_director?.text = film.director
        fragment_film_details_tv_year?.text = film.year
        fragment_film_details_tv_runtime?.text = film.runtime
        fragment_film_details_tv_plot?.text = film.plot
        fragment_film_details_tv_awards?.text = film.awards
        fragment_film_details_tv_cast?.text = film.actors
        fragment_film_details_tv_genre?.text = film.genre
        fragment_film_details_tv_imdbScore?.text = film.imdbRating
        fragment_film_details_tv_metacriticScore?.text = film.metascore
        fragment_film_details_tv_language?.text = film.language

        // Un-hide Views
        fragment_film_details_tv_title?.visibility = View.VISIBLE
        fragment_film_details_tv_director?.visibility = View.VISIBLE
        fragment_film_details_tv_year?.visibility = View.VISIBLE
        fragment_film_details_tv_runtime?.visibility = View.VISIBLE
        fragment_film_details_tv_plot?.visibility = View.VISIBLE
        fragment_film_details_tv_awards?.visibility = View.VISIBLE
        fragment_film_details_tv_cast?.visibility = View.VISIBLE
        fragment_film_details_tv_genre?.visibility = View.VISIBLE
        fragment_film_details_tv_imdbScore?.visibility = View.VISIBLE
        fragment_film_details_tv_metacriticScore?.visibility = View.VISIBLE
        fragment_film_details_tv_language?.visibility = View.VISIBLE
        fragment_film_details_iv_poster?.visibility = View.VISIBLE
        fragment_film_details_tv_DIRECTEDBY?.visibility = View.VISIBLE
        fragment_film_details_tv_METACRITIC?.visibility = View.VISIBLE
        fragment_film_details_tv_IMDB?.visibility = View.VISIBLE
        fragment_film_details_tv_STARRING?.visibility = View.VISIBLE


        if (fragment_film_details_iv_poster != null) {
            Picasso.get().load(film.posterURL).error(R.drawable.ic_image_loading_darkgreen_48dp)
                    .placeholder(R.drawable.ic_image_loading_darkgreen_48dp).into(fragment_film_details_iv_poster)
        }
    }

    companion object {

        private const val ARG_IMDBID = "imdbID"

        fun newInstance(imdbID: String): FilmDetailsFragment {
            val fragment = FilmDetailsFragment()
            val args = Bundle()
            args.putString(ARG_IMDBID, imdbID)
            fragment.arguments = args
            return fragment
        }
    }
}
