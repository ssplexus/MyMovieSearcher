package ru.ssnexus.mymoviesearcher.domain

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.ssnexus.mymoviesearcher.data.API
import ru.ssnexus.mymoviesearcher.data.MainRepository
import ru.ssnexus.mymoviesearcher.data.TmdbApi
import ru.ssnexus.mymoviesearcher.data.TmdbResultsDto
import ru.ssnexus.mymoviesearcher.utils.Converter
import ru.ssnexus.mymoviesearcher.viewmodel.HomeFragmentViewModel

class Interactor(val repo: MainRepository, val retrofitService: TmdbApi) {

    //В конструктор мы будем передавать коллбэк из вью модели, чтобы реагировать на то, когда фильмы будут получены
    //и страницу, которую нужно загрузить (это для пагинации)
    fun getFilmsFromApi(page: Int, callback: HomeFragmentViewModel.ApiCallback) {
        retrofitService.getFilms(API.KEY, "ru-RU", page).enqueue(object : Callback<TmdbResultsDto> {
            override fun onResponse(call: Call<TmdbResultsDto>, response: Response<TmdbResultsDto>) {
                //При успехе мы вызываем метод передаем onSuccess и в этот коллбэк список фильмов
                callback.onSuccess(Converter.convertApiListToDtoList(response.body()?.tmdbFilms))
            }

            override fun onFailure(call: Call<TmdbResultsDto>, t: Throwable) {
                //В случае провала вызываем другой метод коллбека
                callback.onFailure()
            }
        })
    }

    fun getFavorites() : List<Film>
    {
        val favList = ArrayList<Film>(repo.filmsDataBase)
        return favList?.filter { it.isInFavorites }
    }


}