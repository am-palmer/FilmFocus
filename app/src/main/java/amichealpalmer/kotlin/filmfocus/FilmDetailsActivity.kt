package amichealpalmer.kotlin.filmfocus

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_film_details.*

class FilmDetailsActivity : BaseActivity() {
    // Display film details in activity_film_details


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_film_details)

        activateToolbar(true) //?

        // Get details from intent
        val film = intent.extras?.getParcelable<Film>(
            FILM_DETAILS_TRANSFER
        ) as Film

        // Set text fields
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
        Picasso.get().load(film.posterURL).error(R.drawable.placeholder_imageloading)
            .placeholder(R.drawable.placeholder_imageloading).into(activity_film_details_iv_poster)

    }


}