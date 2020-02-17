package amichealpalmer.kotlin.filmfocus

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_film_details.*

class FilmDetailsActivity : BaseActivity() {
    // Display film details in activity_film_details

    private val TAG = "FilmDetailsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, ".onCreate started")
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_film_details)

        // Get imdbID from intent, use it to get film details
        val imdbID = intent.extras?.getString("imdbID")
        if (imdbID != null) {
            FilmSearch(this).getFilmByID(imdbID)
        } else {
            Log.e(TAG, ".onCreate: failed to retrieve imdbID from intent extras.")
        }

//        activateToolbar(true) //?

        // Get details from intent
//        val film = intent.extras?.getParcelable<Film>(
//            FILM_DETAILS_TRANSFER
//        ) as Film

    }


    fun onFilmInfoDownload(film: Film) { // todo: a different interface for tv shows. they have different values. will need a TVShow object
        Log.d(TAG, "retrieved film information: ${film}")
        Log.d(TAG, "film title: ${film.title}")
        // Set text fields
        Log.d(TAG, "Setting text fields for the layout")
        activity_film_details_tv_title.text = film.title
        activity_film_details_tv_director.text = film.director
        activity_film_details_tv_year.text = film.year
        activity_film_details_tv_runtime.text = film.runtime
        activity_film_details_tv_plot.text = film.plot
        activity_film_details_tv_awards.text = film.awards
        activity_film_details_tv_cast.text = film.actors
        activity_film_details_tv_genre.text = film.genre
        activity_film_details_tv_imdbScore.text = film.imdbRating
        activity_film_details_tv_metacriticScore.text = film.metascore
        activity_film_details_tv_language.text = film.language


        // Load the poster
        Log.d(TAG, "Picasso: setting poster url for the layout")
        Picasso.get().load(film.posterURL).error(R.drawable.placeholder_imageloading)
                .placeholder(R.drawable.placeholder_imageloading).into(activity_film_details_iv_poster)

        Log.d(TAG, ".onCreate finished")
    }


}