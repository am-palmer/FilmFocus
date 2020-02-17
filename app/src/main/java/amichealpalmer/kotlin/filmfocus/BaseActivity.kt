package amichealpalmer.kotlin.filmfocus

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

internal const val OMDB_SEARCH_QUERY = "OMDB_SEACH_QUERY"
internal const val FILM_DETAILS_TRANSFER = "FILM_DETAILS_TRANSFER"

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() { // todo: abstract this class
    private val TAG = "BaseActivity"

    internal fun activateToolbar(enableHome: Boolean) {
        Log.d(TAG, ".activateToolbar")

        var toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDefaultDisplayHomeAsUpEnabled(enableHome)
    }

}