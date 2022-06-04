package ru.ssnexus.mymoviesearcher.domain

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.ssnexus.mymoviesearcher.data.*
import ru.ssnexus.mymoviesearcher.data.entity.TmdbResultsDto
import ru.ssnexus.mymoviesearcher.data.preferences.PreferenceProvider
import ru.ssnexus.mymoviesearcher.viewmodel.HomeFragmentViewModel
import timber.log.Timber

class Interactor(val repo: MainRepository, val retrofitService: TmdbApi, private val preferences: PreferenceProvider) {
    private var lastPage:Int = 0
    //В конструктор мы будем передавать коллбэк из вью модели, чтобы реагировать на то, когда фильмы будут получены
    //и страницу, которую нужно загрузить (это для пагинации)
    fun getFilmsFromApi(page: Int, callback: HomeFragmentViewModel.ApiCallback) {
        //Исколючить множественный вызов одной и той же страницы
       // if(page == lastPage) return
        //lastPage = page
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

    fun lastSelectedPageReset(){
        lastPage = 0
    }

    fun getLastSelectedPage() = lastPage

    fun getFavorites() : List<Film>
    {
        return repo.favoritesFilms
    }

    fun addToFavorites (film : Film){
        if(!repo.favoritesFilms.contains(film))
            repo.favoritesFilms.add(film)
    }

    fun removeFromFavorites (film:Film)
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


