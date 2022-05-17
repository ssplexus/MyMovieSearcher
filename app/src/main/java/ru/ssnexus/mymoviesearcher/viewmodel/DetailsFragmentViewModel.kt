package ru.ssnexus.mymoviesearcher.viewmodel

import androidx.lifecycle.ViewModel
import org.koin.core.KoinComponent
import org.koin.core.inject
import ru.ssnexus.mymoviesearcher.domain.Film
import ru.ssnexus.mymoviesearcher.domain.Interactor

class DetailsFragmentViewModel:ViewModel(), KoinComponent {

    //Инициализируем интерактор
    private val interactor: Interactor by inject()

    fun addToFavorites(film : Film)
    {
        interactor.addToFavorites(film)
    }

    fun removeFromFavorites(film : Film)
    {
        interactor.removeFromFavorites(film)
    }

    fun checkInFavorites(film: Film)
    {
        var favFilms = interactor.repo.favoritesFilms

        film.isInFavorites = false
        if(favFilms.isNotEmpty())
            favFilms.forEach {favFilm->
                if(film.id == favFilm.id) film.isInFavorites = true
            }
    }
}