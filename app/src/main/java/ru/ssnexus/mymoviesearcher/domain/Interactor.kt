package ru.ssnexus.mymoviesearcher.domain

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.ssnexus.mymoviesearcher.App
import ru.ssnexus.mymoviesearcher.data.API
import ru.ssnexus.mymoviesearcher.data.MainRepository
import ru.ssnexus.mymoviesearcher.data.TmdbApi
import ru.ssnexus.mymoviesearcher.data.TmdbResultsDto
import ru.ssnexus.mymoviesearcher.viewmodel.HomeFragmentViewModel
import timber.log.Timber

class Interactor(val repo: MainRepository, val retrofitService: TmdbApi) {
    private var lastPage:Int = 0
    //В конструктор мы будем передавать коллбэк из вью модели, чтобы реагировать на то, когда фильмы будут получены
    //и страницу, которую нужно загрузить (это для пагинации)
    fun getFilmsFromApi(page: Int, callback: HomeFragmentViewModel.ApiCallback) {
        //Исколючить множественный вызов одной и той же страницы
        if(page == lastPage) return
        lastPage = page
        retrofitService.getFilms(API.KEY, "ru-RU", page).enqueue(object : Callback<TmdbResultsDto> {
            override fun onResponse(call: Call<TmdbResultsDto>, response: Response<TmdbResultsDto>) {
                //При успехе мы вызываем метод передаем onSuccess и в этот коллбэк список фильмов
                callback.onSuccess(response.body())
            }

            override fun onFailure(call: Call<TmdbResultsDto>, t: Throwable) {
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



}