package amichealpalmer.kotlin.filmfocus.utilities.sharedprefs

import amichealpalmer.kotlin.filmfocus.utilities.LocalDateSerializer
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import org.joda.time.LocalDate

// Template class for retrieving objects from Shared Preferences

abstract class BaseSharedPrefUtil(context: Context) {

    private val SHARED_PREFS = "sharedPrefs"
    protected val sharedPreferences: SharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
    protected val gsonBuilder = GsonBuilder()
    protected val gson = gsonBuilder.create()

    init {
        // Allow us to load and save objects which make use of the LocalDate object
        gsonBuilder.registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
    }

}