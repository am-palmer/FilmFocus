package amichealpalmer.kotlin.filmfocus.model.remote

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.model.remote.json.GetJSONSearch
import android.app.Application
import androidx.lifecycle.MutableLiveData
import java.lang.ref.WeakReference

class FilmThumbnailRepository(val application: Application) {

    private val resultList: MutableLiveData<ArrayList<FilmThumbnail?>> by lazy { MutableLiveData<ArrayList<FilmThumbnail?>>(ArrayList()) }

    // LiveData objects holding search parameters
    private val query: MutableLiveData<String> by lazy { MutableLiveData<String>("default") }
    private val currentPage: MutableLiveData<Int> by lazy { MutableLiveData<Int>(1) }
    private val haveMoreResults: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>(true) }

    // Called by the API accessor to update the resultList
    fun updateResults(newResults: ArrayList<FilmThumbnail?>) {
        if (newResults.isNullOrEmpty()) {
            haveMoreResults.value = false // We note that there are no more results for this query
            return
        }
        for (result in newResults) {
            if (result != null) { // This check might not be needed
                resultList.value?.add(result)
            }
        }
    }

    fun newQuery(query: String) {
        // Clear the resultList
        resultList.value?.clear()
        // Reset the parameter objects
        this.query.value = query
        currentPage.value = 1
        haveMoreResults.value = true
    }

    fun getNextPage() {
        // Create a new AsyncTask which will notify this object once it has finished
        if (haveMoreResults.value == true) {
            val searchString = "?s=${query.value}&page=${currentPage.value}"
            GetJSONSearch(WeakReference(this), application.getString(R.string.OMDB_API_KEY)).execute(searchString)
            currentPage.value = currentPage.value!! + 1
        }
    }


    val getResults get() = resultList
    val getQuery get() = query
    val getCurrentPage get() = currentPage
    val getHaveMoreResults get() = haveMoreResults

}