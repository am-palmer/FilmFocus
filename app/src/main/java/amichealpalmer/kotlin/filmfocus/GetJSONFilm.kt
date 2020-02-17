package amichealpalmer.kotlin.filmfocus

import android.util.Log
import org.json.JSONException
import org.json.JSONObject

class GetJSONFilm(val listener: FilmSearch, val apikey: String) :
        GetJSONBase<Film?>() { // Retrieve OMDB JSON Film Data and return it to the calling class.

    val TAG = "GetJSONFilm"

    override fun onPostExecute(result: Film?) {
        Log.d(TAG, ".onPostExecute starts")
        if (result != null) {
            listener.onFilmInfoDownloadComplete(result)
        } else {
            Log.d(TAG, ".onPostExecute: result object is null.")
        }

    }

    override fun doInBackground(vararg params: String): Film? { // params[0] should be imdbID
        val query = "?i=${params[0]}" // i= Search by IMDB id.
        Log.d(TAG, ".doInBackground started")
        var defaultResult = null // todo better handling of nullability

        // Get our JSON object from the parent class
        Log.d(TAG, "calling super.getJSONDataObject and passing our search query")
        var JSONResult = super.getJSONDataObject(apikey, query)

        if (JSONResult != null) {
            Log.d(TAG, "JSONResult not null")
            return createFilmFromJSON(JSONResult)
        } else {
            Log.d(TAG, "JSONResult is null")
            return defaultResult
        }
    }

    private fun createFilmFromJSON(jsonItem: JSONObject): Film? {
        Log.d(TAG, ".createFilmFromJSON starting")
        var film: Film?
        try {
            val title = jsonItem.getString("Title")
            val year = jsonItem.getString("Year")
            val rated = jsonItem.getString("Rated")
            val released = jsonItem.getString("Released")
            val runtime = jsonItem.getString("Runtime")
            val genre = jsonItem.getString("Genre")
            val director = jsonItem.getString("Director")
            //val writer = jsonItem.getString("Writer")
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