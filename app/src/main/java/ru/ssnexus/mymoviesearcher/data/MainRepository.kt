package ru.ssnexus.mymoviesearcher.data

import io.reactivex.rxjava3.core.Observable
import ru.ssnexus.mymoviesearcher.data.DAO.FilmDao
import ru.ssnexus.mymoviesearcher.data.entity.Film
import timber.log.Timber

class MainRepository(private val filmDao: FilmDao) {
    val favoritesFilms = mutableListOf<Film>()

    fun putToDb(films: List<Film>) {
        //Запросы в БД должны быть в отдельном потоке
        filmDao.insertAll(films)

    }

    fun getAllFromDB(): Observable<List<Film>> = filmDao.getCachedFilms()

    fun getAllFromDBAsList(): List<Film> = filmDao.getCachedFilmsList()

    fun getSize() : Int = filmDao.getData().size

    fun clearCache()
    {
        Timber.d("ClearCache")
        filmDao.nukeTable()
    }
}
