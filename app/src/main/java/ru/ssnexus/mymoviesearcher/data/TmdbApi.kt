package ru.ssnexus.mymoviesearcher.data

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.ssnexus.mymoviesearcher.data.entity.TmdbResultsDto


interface TmdbApi {
    @GET("3/movie/{category}")
    fun getFilms(
        @Path("category") category: String,
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ): retrofit2.Call<TmdbResultsDto>
}