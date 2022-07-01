package ru.ssnexus.mymoviesearcher.viewmodel

import androidx.lifecycle.ViewModel
import ru.ssnexus.mymoviesearcher.App
import ru.ssnexus.mymoviesearcher.data.entity.Film
import ru.ssnexus.mymoviesearcher.domain.Interactor
import javax.inject.Inject

class DetailsFragmentViewModel:ViewModel() {

    //Инициализируем интерактор
    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
    }
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