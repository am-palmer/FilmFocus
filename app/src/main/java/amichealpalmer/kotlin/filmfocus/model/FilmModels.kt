package amichealpalmer.kotlin.filmfocus.model

import android.os.Parcel
import android.os.Parcelable

// Class holding all the models relating to film objects.

// The most verbose Film model, containing all relevant details about a film, which is fetched when we load a FilmDetailDialogFragment
class Film(
        val title: String, // e.g. Guardians of the Galaxy Vol. 2
        val imdbID: String,
        val year: String, // e.g. 2017
        val rated: String, // PG-13
        val released: String, // 05 May 2017
        val runtime: String, // 136 min
        val genre: String, // Action, Adventure, Comedy, Sci-Fi
        val director: String, // James Gunn
        val actors: String, // Chris Pratt, Zoe Saldana, Dave Bautista, Vin Diesel
        val plot: String,  // The Guardians struggle to keep together as a team while dealing with [...]
        val language: String, // English
        val country: String, // USA
        val awards: String, // Nominated for 1 Oscar. Another 14 wins & 52 nominations.
        val posterURL: String, // https://m.media-amazon.com/images/M/MV5BNjM0NTc0NzItM2FlYS00YzEwLWE0YmUtNTA2ZWIzODc2OTgxXkEyXkFqcGdeQXVyNTgwNzIyNzg@._V1_SX300.jpg
        val metascore: String, // 67
        val imdbRating: String, // 7.6
        val type: String // movie

) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(imdbID)
        parcel.writeString(year)
        parcel.writeString(rated)
        parcel.writeString(released)
        parcel.writeString(runtime)
        parcel.writeString(genre)
        parcel.writeString(director)
        parcel.writeString(actors)
        parcel.writeString(plot)
        parcel.writeString(language)
        parcel.writeString(country)
        parcel.writeString(awards)
        parcel.writeString(posterURL)
        parcel.writeString(metascore)
        parcel.writeString(imdbRating)
        parcel.writeString(type)
    }

    override fun toString(): String {
        return "Film(title='$title', imdbID='$imdbID' year='$year', rated='$rated', released='$released', runtime='$runtime', genre='$genre', director='$director', actors='$actors', plot='$plot', language='$language', country='$country', awards='$awards', posterURL='$posterURL', metascore='$metascore', imdbRating='$imdbRating', type='$type')"
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Film> {
        override fun createFromParcel(parcel: Parcel): Film {
            return Film(parcel)
        }

        override fun newArray(size: Int): Array<Film?> {
            return arrayOfNulls(size)
        }
    }
}

/* A simpler model which stores basic information about a film, created from the JSON information we retrieve from OMDB.
   These are displayed in the RecyclerViews throughout the app.
 */

open class FilmThumbnail(val title: String,
                         val year: String,
                         val imdbID: String,
                         val type: String,
                         val posterURL: String) : Parcelable {
    override fun toString(): String {
        return "FilmThumbnail(title='$title', year='$year', imdbID='$imdbID', type='$type', posterURL='$posterURL')"
    }

    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(year)
        parcel.writeString(imdbID)
        parcel.writeString(type)
        parcel.writeString(posterURL)
    }

    override fun equals(other: Any?): Boolean {
        return if (javaClass == other?.javaClass) {
            other as FilmThumbnail
            (this.title == other.title &&
                    this.posterURL == other.posterURL &&
                    this.type == other.type &&
                    this.imdbID == other.imdbID &&
                    this.year == other.year)
        } else super.equals(other)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FilmThumbnail> {
        override fun createFromParcel(parcel: Parcel): FilmThumbnail {
            return FilmThumbnail(parcel)
        }

        override fun newArray(size: Int): Array<FilmThumbnail?> {
            return arrayOfNulls(size)
        }
    }
}
