package ru.ssnexus.mymoviesearcher.domain

import androidx.lifecycle.LiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.ssnexus.mymoviesearcher.data.*
import ru.ssnexus.mymoviesearcher.data.entity.Film
import ru.ssnexus.mymoviesearcher.data.entity.TmdbResultsDto
import ru.ssnexus.mymoviesearcher.data.preferences.PreferenceProvider
import ru.ssnexus.mymoviesearcher.viewmodel.HomeFragmentViewModel
import timber.log.Timber
import java.util.concurrent.Executors

class Interactor(val repo: MainRepository, val retrofitService: TmdbApi, private val preferences: PreferenceProvider) {

    //В конструктор мы будем передавать коллбэк из вью модели, чтобы реагировать на то, когда фильмы будут получены
    //и страницу, которую нужно загрузить (это для пагинации)
    fun getFilmsFromApi(page: Int, callback: HomeFragmentViewModel.ApiCallback) {
        Timber.d("Get Films From API")
        retrofitService.getFilms(getDefaultCategoryFromPreferences(), API.KEY, "ru-RU", page).enqueue(object : Callback<TmdbResultsDto> {
            override fun onResponse(call: Call<TmdbResultsDto>, response: Response<TmdbResultsDto>) {
                //При успехе мы вызываем метод передаем onSuccess и в этот коллбэк список фильмов
                Timber.d("Get Films Success")
                callback.onSuccess(response.body())

            }

            override fun onFailure(call: Call<TmdbResultsDto>, t: Throwable) {
                Timber.d("Get Films Failure")
                //В случае провала вызываем другой метод коллбека
                callback.onFailure()
            }
        })
    }

    fun getFilmsFromDB(): LiveData<List<Film>> = repo.getAllFromDB()

    fun getDBSize(): Int = repo.getSize()

    fun clearCache()
    {
        Executors.newSingleThreadExecutor().execute {
            repo.clearCache()
        }
    }

    fun addFilmsToDB(films : List<Film>) {
        Executors.newSingleThreadExecutor().execute {
            //Кладем фильмы в бд
            repo.putToDb(films)
        }
    }

    fun getFavorites() : List<Film>
    {
        return repo.favoritesFilms
    }

    fun updateFavorites(srcList: List<Film>)
    {
        // сверямеся со списком избранных если фильм в списке актуализируем признак isInFavorites
        var favFilms = repo.favoritesFilms
        srcList.forEach {film->
            film.isInFavorites = false
            if(favFilms.isNotEmpty())
                favFilms.forEach {favFilm->
                    if(film.id == favFilm.id) film.isInFavorites = true
                }
        }
    }

    fun addToFavorites (film : Film){
        if(!repo.favoritesFilms.contains(film))
            repo.favoritesFilms.add(film)
    }

    fun removeFromFavorites (film: Film)
    {
        var removeIdx:Int = repo.favoritesFilms.indexOf(film)
        if(removeIdx >= 0) repo.favoritesFilms.removeAt(removeIdx)
    }

    //Метод для сохранения настроек
    fun saveDefaultCategoryToPreferences(category: String) {
        preferences.saveDefaultCategory(category)
    }
    //Метод для получения настроек
    fun getDefaultCategoryFromPreferences() = preferences.getDefaultCategory()
}


