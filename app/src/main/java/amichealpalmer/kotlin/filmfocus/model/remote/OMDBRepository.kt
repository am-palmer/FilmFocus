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

// todo: null safety

class OMDBRepository(private val context: Context) {

    // We use lazy for these objects as they may not be accessed, and in that case we avoid the cost of initializing them - e.g. if the user opens the app but does not make any search queries that session
    private val resultList: MutableLiveData<ArrayList<FilmThumbnail?>> by lazy { MutableLiveData<ArrayList<FilmThumbnail?>>(ArrayList()) }

    // LiveData objects holding search parameters
    private val query: MutableLiveData<String?> by lazy { MutableLiveData<String?>(null) }
    private val currentPageNumber: MutableLiveData<Int> by lazy { MutableLiveData(1) }
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

            // todo null safety
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

                    // todo: if this is the first query with this term, check totalResults value, use it to derive page count ->
                    //  10 results per page, then totalResults / 10 (rounded up to next positive integer) gives page count

                    // Add results to our LiveData
                    if (response.body() != null) {
                        updateResults(response.body()?.search ?: return)
                    }

                }
            })

            currentPageNumber.value = currentPageNumber.value!! + 1
        }
    }

    // todo: above and below methods have redundant calls, wasteful, save these variables instead

    fun getFilmDetails(callback: FilmDetailListener, imdbID: String) {
        val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val searchApi = retrofit.create(OMDBSearchApi::class.java)

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
                    callback.onFilmDetailsRetrieved(response.body()!!)
                }

            }
        })
    }

    interface FilmDetailListener {
        fun onFilmDetailsRetrieved(film: Film)
    }

    val getResults get() = resultList
    val getHaveMoreResults get() = haveMoreResults


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