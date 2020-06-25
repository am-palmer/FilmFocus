package amichealpalmer.kotlin.filmfocus.model.remote

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.model.Film
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
import kotlin.math.ceil

class OMDBRepository(private val context: Context) {

    // We use lazy for these objects as they may not be accessed, and in that case we avoid the cost of initializing them - e.g. if the user opens the app but does not make any search queries that session
    private val resultList: MutableLiveData<ArrayList<FilmThumbnail?>> by lazy { MutableLiveData<ArrayList<FilmThumbnail?>>(ArrayList()) }
    private val searchApi: OMDBSearchApi by lazy { Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build().create(OMDBSearchApi::class.java) }
    private val film: MutableLiveData<Film?> by lazy {MutableLiveData<Film?>(null)}

    // LiveData objects holding search parameters
    private val query: MutableLiveData<String?> by lazy { MutableLiveData<String?>(null) }
    private val nextPageNumber: MutableLiveData<Int> by lazy { MutableLiveData(1) }
    private var maxPageNumber: Int? = null
    private val haveMoreResults: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>(true) }
    private val currentlyLoadingResults: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>(false) } // Mutex lock to stop the UI making multiple requests.

    // Called by the API accessor to update the resultList
    fun updateResults(newResults: List<FilmThumbnail?>) {
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
        nextPageNumber.value = 1
        maxPageNumber = null
        haveMoreResults.value = true
        currentlyLoadingResults.value = false
    }

    fun getNextPage() {
        // maxPageNumber is null on the first call to this method, on subsequent calls we check to see if there are any more pages of results to query for. Pages contain 10 results each
        currentlyLoadingResults.value = true
        if (maxPageNumber == null || nextPageNumber.value!! <= maxPageNumber!!) {

            val call = searchApi.getSearchResults(context.getString(R.string.OMDB_API_KEY), query.value!!, nextPageNumber.value!!)

            call.enqueue(object : Callback<SearchResponse> {
                override fun onFailure(call: Call<SearchResponse>?, t: Throwable?) {
                    Log.e(TAG, "retrofit callback error: ${t?.message}")
                }

                override fun onResponse(call: Call<SearchResponse>?, response: Response<SearchResponse>) {
                    if (!response.isSuccessful) {
                        Log.e(TAG, "response: unsuccessful -> code ${response.code()}")
                        return
                    }

                    if (maxPageNumber == null) {
                        // Compute the max page number for further queries
                        maxPageNumber = ceil((response.body()!!.totalResults / 10.0).toDouble()).toInt()
                        Log.d(TAG, ".onResponse: max page number computed. Max page number: $maxPageNumber")
                    }

                    // Add results to our LiveData
                    if (response.body() != null) {
                        updateResults(response.body()?.search ?: return)
                    }

                    // Increment page number for next call
                    Log.d(TAG, ".onResponse: incrementing next page number from $nextPageNumber to ${nextPageNumber.value!! + 1}")
                    nextPageNumber.value = nextPageNumber.value!! + 1
                    if (nextPageNumber.value!! > maxPageNumber!!) {
                        Log.d(TAG, ".onResponse: nextpageNumber ${nextPageNumber.value}  is higher than maxPageNumber ${maxPageNumber}. setting haveMoreResults to false")
                        haveMoreResults.value = false // We prevent further calls from being made as we have retrieved all pages
                    }

                    currentlyLoadingResults.value = false

                }
            })

        }
    }

    // Get details about a film. Used by FilmDetailDialog to display more information when a film is tapped
    fun requestFilmDetails(imdbID: String) {

        val call = searchApi.getMovieDetails(context.getString(R.string.OMDB_API_KEY), imdbID)

        call.enqueue(object : Callback<Film> {

            override fun onFailure(call: Call<Film>, t: Throwable) {
                Log.e(TAG, "retrofit callback error (film details): ${t?.message}")
            }

            override fun onResponse(call: Call<Film>, response: Response<Film>) {
                if (!response.isSuccessful) {
                    Log.e(TAG, "response: unsuccessful -> code ${response.code()}")
                    return
                }
                if (response.body() != null && response.body()!!::class.java != Film::class.java) {
                    Log.e(TAG, "response: no information for this film")
                } else {
                    // Update the MutableLiveData film item
                    film.value = response.body()

                    //callback.onFilmDetailsRetrieved(response.body()!!)
                }

            }
        })
    }

    fun clearFilm(){
        film.value = null
    }

    val getResults get() = resultList
    val getHaveMoreResults get() = haveMoreResults
    val getCurrentlyLoadingResults get() = currentlyLoadingResults.value
    val getFilm get() = film


    companion object {

        private const val TAG = "FilmThumbRepo"
        private const val baseUrl = "https://www.omdbapi.com/"

        @Volatile
        private var instance: OMDBRepository? = null

        fun getInstance(context: Context) =
                instance ?: synchronized(this) {
                    instance ?: OMDBRepository(context).also { instance = it }
                }
    }

}