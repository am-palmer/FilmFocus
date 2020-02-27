package amichealpalmer.kotlin.filmfocus.activities

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.data.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.fragments.BrowseFragment
import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import java.util.*

abstract class BrowseActivity : FragmentActivity() { // Parent Fragment for fragments which utilize a list of results (FilmThumbnails)
// todo: convert to pure fragment?
    private val TAG = "BrowseActivity"

    fun displayBrowseFragment(resultList: ArrayList<FilmThumbnail?>) {
        Log.d(TAG, ".displayBrowseFragment starts.")
        setContentView(R.layout.content_main)
        // val results = resultList as List<FilmThumbnail>
        Log.d(TAG, ".displayBrowseFragment: instantiating new fragment")
        val fragment = BrowseFragment()

        Log.d(TAG, ".displayBrowseFragment: putting result list in bundle")
        var args = Bundle()
        args.putParcelableArrayList("resultList", resultList)

        Log.d(TAG, ".displayBrowseFragment: beginning transaction")
        fragment.arguments = args
        var transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.main_frame_layout_fragment_holder, fragment) // Defined in activity_main.xml
        transaction.commit()
        Log.d(TAG, ".displaySearchResults complete")
    }

}