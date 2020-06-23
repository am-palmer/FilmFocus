package amichealpalmer.kotlin.filmfocus.model.remote

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.model.SearchResponse
import amichealpalmer.kotlin.filmfocus.model.remote.json.OMDBSearchApi
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// todo: null safety

class FilmThumbnailRepository(private val context: Context) {

    // We use lazy for these objects as they may not be accessed, and in that case we avoid the cost of initializing them - e.g. if the user opens the app but does not make any search queries that session
    private val resultList: MutableLiveData<ArrayList<FilmThumbnail?>> by lazy { MutableLiveData<ArrayList<FilmThumbnail?>>(ArrayList()) }

    // LiveData objects holding search parameters
    private val query: MutableLiveData<String?> by lazy { MutableLiveData<String?>(null) }
    private val currentPageNumber: MutableLiveData<Int> by lazy { MutableLiveData<Int>(1) }
    private val haveMoreResults: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>(true) }

    // Called by the API accessor to update the resultList
    fun updateResults(newResults: List<FilmThumbnail?>) {
        if (newResults.isNullOrEmpty()) {
            haveMoreResults.value = false // We note that there are no more results for this query, listener for haveMoreResults in fragment updates view (hide spinner)
            return
        }
        for (result in newResults) {
            if (result != null) {
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
            //val searchString = "?s=${query.value}&page=${currentPageNumber.value}"
            //GetJSONSearch(WeakReference(this), context.getString(R.string.OMDB_API_KEY)).execute(searchString)

            val retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            val searchApi = retrofit.create(OMDBSearchApi::class.java)

            val call = searchApi.getSearchResults(context.getString(R.string.OMDB_API_KEY), query.value!!, currentPageNumber.value!!)

            call.enqueue(object : Callback<SearchResponse> {
                override fun onFailure(call: Call<SearchResponse>?, t: Throwable?) {
                    Log.e(TAG, "retrofit callback error: ${t?.message}")
                }

                override fun onResponse(call: Call<SearchResponse>?, response: Response<SearchResponse>) {
                    if (!response.isSuccessful) {
                        Log.e(TAG, "response: unsuccessful -> code ${response.code()}")
                        return
                    }

                    Log.d(TAG, ".onResponse: total results is ${response.body()?.totalResults}")
                    Log.d(TAG, "onResponse: search object null? ${response.body()?.search == null}")
                    Log.d(TAG, "onResponse: search object tostring? ${response.body()?.search.toString()}")
                    Log.d(TAG, ".onResponse: this results arraylist is size ${response.body()?.search?.size}")

                    // Add results to our LiveData
                    if (response.body() != null) {
                        updateResults(response.body()?.search ?: return)
                    }

                }
            })

            currentPageNumber.value = currentPageNumber.value!! + 1
        }
    }

    val getResults get() = resultList
    val getHaveMoreResults get() = haveMoreResults


    companion object {

        private const val TAG = "FilmThumbRepo"
        private const val baseUrl = "https://www.omdbapi.com/"

        @Volatile
        private var instance: FilmThumbnailRepository? = null

        fun getInstance(context: Context) =
                instance ?: synchronized(this) {
                    instance ?: FilmThumbnailRepository(context).also { instance = it }
                }
    }

}