package ru.ssnexus.mymoviesearcher.data

import ru.ssnexus.mymoviesearcher.data.DAO.FilmDao
import ru.ssnexus.mymoviesearcher.data.entity.Film
import timber.log.Timber

class MainRepository(private val filmDao: FilmDao) {
    val favoritesFilms = mutableListOf<Film>()

    fun putToDb(films: List<Film>) {
        //Запросы в БД должны быть в отдельном потоке
        filmDao.insertAll(films)

    }

    fun getAllFromDB(): List<Film> {
        return filmDao.getCachedFilms()
    }

    fun clearCache()
    {
        Timber.d("ClearCache")
        filmDao.nukeTable()
    }
}
