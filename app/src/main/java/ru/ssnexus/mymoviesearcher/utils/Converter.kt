package ru.ssnexus.mymoviesearcher.utils

import ru.ssnexus.remote_module.entity.TmdbFilm
import ru.ssnexus.database_module.data.entity.Film
import ru.ssnexus.remote_module.entity.FilmDto

object Converter {
    fun convertApiListToDtoList(list: List<TmdbFilm>?): List<Film> {
        val result = mutableListOf<Film>()
        list?.forEach {
            result.add(
                Film(
                id = it.id,
                title = it.title,
                poster = it.posterPath,
                description = it.overview,
                rating = it.voteAverage,
                isInFavorites = false
            )
            )
        }
        return result
    }

    fun convertApiToDto(filmDto:FilmDto): Film {
        return Film(
                    id = filmDto.id,
                    title = filmDto.title,
                    poster = filmDto.poster_path,
                    description = filmDto.overview,
                    rating = filmDto.vote_average,
                    isInFavorites = false
                )
    }
}