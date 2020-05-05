package amichealpalmer.kotlin.filmfocus.utilities.json

import amichealpalmer.kotlin.filmfocus.model.Film
import amichealpalmer.kotlin.filmfocus.view.FilmDetailFragment
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference

// Retrieve OMDB JSON Film Data and return it to the calling class.
// todo: is there a situation where the weakreferences are garbage collected when we don't want them to be?
class GetJSONFilm(private var listener: WeakReference<FilmDetailFragment>, private val apikey: String) :
        GetJSONBase<Film?>() {

    val TAG = "GetJSONFilm"

    override fun onPostExecute(result: Film?) {
        Log.d(TAG, ".onPostExecute starts")
        if (result != null) {
            listener.get()?.onFilmInfoDownload(result)
        } else {
            Log.e(TAG, ".onPostExecute: result object is null.")
        }
    }

    override fun doInBackground(vararg params: String): Film? { // params[0] should be imdbID
        val query = "?i=${params[0]}" // i= Search by IMDB id.
        Log.d(TAG, ".doInBackground started")

        // Get our JSON object from the parent class
        return try {
            val JSONResult = super.getJSONDataObject(apikey, query)
            createFilmFromJSON(JSONResult!!)
        } catch (e: NullPointerException) {
            Log.e(TAG, ".doInBackground: NPE")
            null
        }

    }

    private fun createFilmFromJSON(jsonItem: JSONObject): Film? {
        Log.d(TAG, ".createFilmFromJSON starting")
        val film: Film?
        try {
            val title = jsonItem.getString("Title")
            val year = jsonItem.getString("Year")
            val rated = jsonItem.getString("Rated")
            val released = jsonItem.getString("Released")
            val runtime = jsonItem.getString("Runtime")
            val genre = jsonItem.getString("Genre")
            val director = jsonItem.getString("Director")
            val actors = jsonItem.getString("Actors")
            val plot = jsonItem.getString("Plot")
            val language = jsonItem.getString("Language")
            val country = jsonItem.getString("Country")
            val awards = jsonItem.getString("Awards")
            val imdbID = jsonItem.getString("imdbID")
            val type = jsonItem.getString("Type")
            val posterURL = jsonItem.getString("Poster")
            val metascore = jsonItem.getString("Metascore")
            val imdbRating = jsonItem.getString("imdbRating")
            film = Film(
                    title,
                    imdbID,
                    year,
                    rated,
                    released,
                    runtime,
                    genre,
                    director,
                    actors,
                    plot,
                    language,
                    country,
                    awards,
                    posterURL,
                    metascore,
                    imdbRating,
                    type
            )
            Log.d(TAG, "Film item constructed: $film")

        } catch (e: JSONException) {
            e.printStackTrace()
            Log.e(TAG, ".createFilmFromJSON: Error processing JSON data. ${e.message}")
            return null
        }

        return film
    }

}