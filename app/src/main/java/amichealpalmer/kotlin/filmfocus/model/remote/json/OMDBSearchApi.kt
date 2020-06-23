package amichealpalmer.kotlin.filmfocus.model.remote.json

import amichealpalmer.kotlin.filmfocus.model.SearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface OMDBSearchApi {

    // http://www.omdbapi.com/?apikey=[apikey]&s=[query]

    //      @GET("/users?filters[0][operator]=equals")
    //          UserDto retrieveUsersByFilters(
    //          @Query("filters[0][field]") String nameFilter,
    //          @Query("filters[0][value]") String value);


//    @GET("?apikey={apikey}&s={query}&page={page}")
//    fun getSearchResults(
//            @Path("apikey") apiKey: String,
//            @Path("query") query: String,
//            @Path("page") page: Int
//    ): Call<List<FilmThumbnail>>

//    @GET("group/{id}/users")
//    fun groupList(@Path("id") groupId: Int): Call<List<User?>?>?

    @GET(".")
    fun getSearchResults(@Query("apikey") apiKey: String,
                         @Query("s") title: String,
                         @Query("page") pageNumber: Int): Call<SearchResponse>

}