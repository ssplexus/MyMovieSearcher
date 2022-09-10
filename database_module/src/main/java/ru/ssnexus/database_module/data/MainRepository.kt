package ru.ssnexus.database_module.data

import io.reactivex.rxjava3.core.Observable
import ru.ssnexus.database_module.data.DAO.FilmDao
import ru.ssnexus.database_module.data.entity.Film
import java.util.concurrent.Executors

//import timber.log.Timber

class MainRepository(private val filmDao: FilmDao) {
    val favoritesFilms = mutableListOf<Film>()

    fun putToDb(films: List<Film>) {
        //Запросы в БД должны быть в отдельном потоке
        filmDao.insertAll(films)

    }

    fun getAllFromDB(): Observable<List<Film>> = filmDao.getCachedFilms()

    fun getFavorites(): List<Film> = filmDao.getFavorites()

    fun getFilmFavStateById(id : Int) : Int = filmDao.getFilmFavStateById(id)

    fun updateFilmFavStateById(id : Int){
        filmDao.updateFavoriteById(id)
    }

    fun getWatchLater(): List<Film> = filmDao.getWatchLater()

    fun getWatchLaterStateById(id : Int) : Int = filmDao.getFilmWatchLaterStateById(id)

    fun updateWatchLaterStateById(id : Int){
        filmDao.updateWatchLaterById(id)
    }

    fun getAllFromDBAsList(): List<Film> = filmDao.getCachedFilmsList()

    fun getSize() : Int = filmDao.getData().size

    fun clearCache()
    {
        //Timber.d("ClearCache")
        filmDao.nukeTable()
    }
}
