package ru.ssnexus.mymoviesearcher.utils

import ru.ssnexus.mymoviesearcher.data.TmdbFilm
import ru.ssnexus.mymoviesearcher.domain.Film

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
}