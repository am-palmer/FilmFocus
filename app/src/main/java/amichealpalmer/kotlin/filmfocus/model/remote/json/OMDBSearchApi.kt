package amichealpalmer.kotlin.filmfocus.model.remote.json

import amichealpalmer.kotlin.filmfocus.model.Film
import amichealpalmer.kotlin.filmfocus.model.SearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface OMDBSearchApi {

    // http://www.omdbapi.com/?apikey=[apikey]&s=[query]

    @GET(".")
    fun getMovieDetails(@Query("apikey") apiKey: String,
                        @Query("i") imdbID: String): Call<Film>

    @GET(".")
    fun getSearchResults(@Query("apikey") apiKey: String,
                         @Query("s") searchQuery: String,
                         @Query("page") pageNumber: Int): Call<SearchResponse>

}