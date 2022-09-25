package ru.ssnexus.remote_module

import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.ssnexus.remote_module.entity.FilmDto
import ru.ssnexus.remote_module.entity.TmdbFilm
import ru.ssnexus.remote_module.entity.TmdbResultsDto


interface TmdbApi {
    @GET("3/movie/{category}")
    fun getFilms(
        @Path("category") category: String,
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ): Observable<TmdbResultsDto>

    @GET("3/search/movie")
    fun getFilmFromSearch(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("query") query: String,
        @Query("page") page: Int
    ): Observable<TmdbResultsDto>

    @GET("3/movie/{id}")
    fun getFilm(
        @Path("id") id: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String
    ): Observable<FilmDto>
}