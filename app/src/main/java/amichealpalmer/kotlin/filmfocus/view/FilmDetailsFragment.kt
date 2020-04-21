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
import kotlinx.android.synthetic.main.fragment_film_details.view.*

private const val ARG_IMDBID = "imdbID"

class FilmDetailsFragment : Fragment() {

    private lateinit var film: Film
    private lateinit var imdbID: String

    private val TAG = "FilmDetailsFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, ".OnCreate called")
        if (arguments != null) {
            Log.d(TAG, "arguments non-null, retrieving film/show details")
            imdbID = arguments!!.getString(ARG_IMDBID) as String
        } else {
            Log.d(TAG, ".onCreate: arguments null")
        }

        // Use id to set film object
        GetJSONFilm(this, getString(R.string.OMDB_API_KEY)).execute(imdbID)

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

    fun onFilmInfoDownload(film: Film) {
        Log.d(TAG, ".onFilmInfoDownload called")
        this.film = film

        // Note that we need to use null safety here, as if user clicks out of the view before the ASyncTask finishes, the views won't exist when this function is called

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

        Log.d(TAG, "Picasso: setting poster url for the layout")
        if (fragment_film_details_iv_poster != null) {
            Picasso.get().load(film.posterURL).error(R.drawable.ic_image_loading_darkgreen_48dp)
                    .placeholder(R.drawable.ic_image_loading_darkgreen_48dp).into(fragment_film_details_iv_poster)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        Log.d(TAG, ".onCreateView called")


        // While the ASyncTask runs, we display the view devoid of information. Perhaps not the most user friendly experience? Might want to display a single loading icon
        val view = inflater.inflate(R.layout.fragment_film_details, container, false)
        Log.d(TAG, "Setting text fields for the layout")

        view.fragment_film_details_tv_title?.text = "..."
        view.fragment_film_details_tv_director?.text = "..."
        view.fragment_film_details_tv_year?.text = "..."
        view.fragment_film_details_tv_runtime?.text = "..."
        view.fragment_film_details_tv_plot?.text = "..."
        view.fragment_film_details_tv_awards?.text = "..."
        view.fragment_film_details_tv_cast?.text = "..."
        view.fragment_film_details_tv_genre?.text = "..."
        view.fragment_film_details_tv_imdbScore?.text = "..."
        view.fragment_film_details_tv_metacriticScore?.text = "..."
        view.fragment_film_details_tv_language?.text = "..."

        //  Log.d(TAG, "Picasso: setting poster url for the layout")
        if (fragment_film_details_iv_poster != null) {
            Picasso.get().load(R.drawable.ic_image_loading_darkgreen_48dp).error(R.drawable.ic_image_loading_darkgreen_48dp)
                    .placeholder(R.drawable.ic_image_loading_darkgreen_48dp).into(view.fragment_film_details_iv_poster)
        }
        return view
    }

    companion object {

        fun newInstance(film: Film): FilmDetailsFragment {
            val fragment = FilmDetailsFragment()
            val args = Bundle()
            args.putParcelable(ARG_IMDBID, film)
            fragment.arguments = args
            return fragment
        }
    }
}
