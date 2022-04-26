package ru.ssnexus.mymoviesearcher.domain

import ru.ssnexus.mymoviesearcher.data.MainRepository
import timber.log.Timber

class Interactor(val repo: MainRepository) {
    fun getFilmsDB(): List<Film> = repo.filmsDataBase

    fun getFavorites() : List<Film>
    {
        val favList = ArrayList<Film>(repo.filmsDataBase)
        return favList?.filter { it.isInFavorites }
    }

    fun addItem(film: Film)
    {
        (repo.filmsDataBase as MutableList<Film>).add(film)
    }
}