package amichealpalmer.kotlin.filmfocus

import java.net.URL

class Film(
    val title: String, // e.g. Guardians of the Galaxy Vol. 2
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
)