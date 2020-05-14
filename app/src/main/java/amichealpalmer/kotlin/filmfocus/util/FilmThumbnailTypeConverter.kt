package amichealpalmer.kotlin.filmfocus.util

import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import androidx.room.TypeConverter
import com.google.gson.Gson

// Converts a FilmThumbnail to a string so it can be saved in Room, using GSON

object FilmThumbnailTypeConverter {

    @TypeConverter
    @JvmStatic
    fun filmToString(filmThumbnail: FilmThumbnail): String {
        val list = arrayOf(
                filmThumbnail.title,
                filmThumbnail.year,
                filmThumbnail.imdbID,
                filmThumbnail.type,
                filmThumbnail.posterURL
        )
        val gson = Gson()
        return gson.toJson(list)
    }

    @TypeConverter
    @JvmStatic
    fun toFilm(string: String): FilmThumbnail {
        val gson = Gson()
        val array: Array<String> = gson.fromJson(string, Array<String>::class.java) as Array<String>
        return FilmThumbnail(array[0], array[1], array[2], array[3], array[4])
    }

}