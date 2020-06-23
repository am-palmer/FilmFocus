package amichealpalmer.kotlin.filmfocus.view

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.model.Film
import amichealpalmer.kotlin.filmfocus.model.remote.OMDBRepository
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_dialog_film_details.*

class FilmDetailDialogFragment : DialogFragment(), OMDBRepository.FilmDetailListener {

    private val ARG_IMDBID = "imdbID"

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
        val view = inflater.inflate(R.layout.fragment_dialog_film_details, container, false)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Use the IMDB ID to retrieve film object Todo: move this logic out of view - create a viewmodel for this fragment
        val imdbID = arguments?.getString(ARG_IMDBID) as String
        val repository = OMDBRepository.getInstance(this.requireContext())

        repository.getFilmDetails(this, imdbID)

        //GetJSONFilm(WeakReference(this), getString(R.string.OMDB_API_KEY)).execute(imdbID)


        fragment_film_details_back_button.setOnClickListener { this.dismiss() }

        super.onViewCreated(view, savedInstanceState)
    }

    // Called by OMDBRepository when Retrofit has retrieved the film information
    override fun onFilmDetailsRetrieved(film: Film) {
        //Log.d(TAG, ".onFilmInfoDownload starts")

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

        // Visibility
        film_details_constraint_layout.visibility = View.VISIBLE
        film_details_progressBar.visibility = View.GONE

        if (fragment_film_details_iv_poster != null) {
            Picasso.get().load(film.posterURL).error(R.drawable.ic_image_loading_grey_48dp)
                    .placeholder(R.drawable.ic_image_loading_grey_48dp).into(fragment_film_details_iv_poster)
        }
    }

    companion object {

        const val TAG = "FilmDetailDialogFrag"

        private const val ARG_IMDBID = "imdbID"

        fun newInstance(imdbID: String): FilmDetailDialogFragment {
            val fragment = FilmDetailDialogFragment()
            val args = Bundle()
            args.putString(ARG_IMDBID, imdbID)
            fragment.arguments = args
            return fragment
        }
    }
}
