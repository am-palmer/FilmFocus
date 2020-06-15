package amichealpalmer.kotlin.filmfocus.model.remote

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.model.remote.json.GetJSONSearch
import android.content.Context
import androidx.lifecycle.MutableLiveData
import java.lang.ref.WeakReference

class FilmThumbnailRepository(private val context: Context) {

    // We use lazy for these objects as they may not be accessed, and in that case we avoid the cost of initializing them - e.g. if the user opens the app but does not make any search queries that session
    private val resultList: MutableLiveData<ArrayList<FilmThumbnail?>> by lazy { MutableLiveData<ArrayList<FilmThumbnail?>>(ArrayList()) }

    // LiveData objects holding search parameters
    private val query: MutableLiveData<String?> by lazy { MutableLiveData<String?>(null) }
    private val currentPageNumber: MutableLiveData<Int> by lazy { MutableLiveData<Int>(1) }
    private val haveMoreResults: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>(true) }

    // Called by the API accessor to update the resultList
    fun updateResults(newResults: ArrayList<FilmThumbnail?>) {
        if (newResults.isNullOrEmpty()) {
            haveMoreResults.value = false // We note that there are no more results for this query
            return
        }
        for (result in newResults) {
            if (result != null) { // This check might not be needed
                val currentList = resultList.value
                currentList?.add(result)
                resultList.value = currentList
            }
        }
    }

    fun newQuery(query: String) {
        // Clear the resultList
        resultList.value?.clear()
        // Reset the parameter objects
        this.query.value = query
        currentPageNumber.value = 1
        haveMoreResults.value = true
    }

    fun getNextPage() {
        // Create a new AsyncTask which will notify this object once it has finished
        if (haveMoreResults.value == true) {
            val searchString = "?s=${query.value}&page=${currentPageNumber.value}"
            GetJSONSearch(WeakReference(this), context.getString(R.string.OMDB_API_KEY)).execute(searchString)
            currentPageNumber.value = currentPageNumber.value!! + 1
        }
    }

    val getResults get() = resultList
    val getQuery get() = query

}