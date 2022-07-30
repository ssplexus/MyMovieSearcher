package ru.ssnexus.mymoviesearcher.domain

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import ru.ssnexus.mymoviesearcher.data.API
import ru.ssnexus.database_module.data.MainRepository
import ru.ssnexus.remote_module.TmdbApi
import ru.ssnexus.database_module.data.entity.Film
import ru.ssnexus.mymoviesearcher.data.preferences.PreferenceProvider
import ru.ssnexus.mymoviesearcher.utils.Converter
import timber.log.Timber

class Interactor(val repo: MainRepository, val retrofitService: TmdbApi, private val preferences: PreferenceProvider) {

    var progressBarState: BehaviorSubject<Boolean> = BehaviorSubject.create()

    fun getFilmsFromApi(page: Int) {
        //Показываем ProgressBar
        progressBarState.onNext(true)
        //Метод getDefaultCategoryFromPreferences() будет получать при каждом запросе нужный нам список фильмов
        retrofitService.getFilms(getDefaultCategoryFromPreferences(), API.KEY, "ru-RU", page)
            .subscribeOn(Schedulers.io())
            .map {
                Converter.convertApiListToDtoList(it.tmdbFilms)
            }
            .subscribeBy(
                onError = {
                    progressBarState.onNext(false)
                },
                onNext = {
                    progressBarState.onNext(false)
                    repo.putToDb(it)
                }
            )
    }

//    //В конструктор мы будем передавать коллбэк из вью модели, чтобы реагировать на то, когда фильмы будут получены
//    //и страницу, которую нужно загрузить (это для пагинации)
//    fun getFilmsFromApi(page: Int) {
//        Timber.d("Get Films From API")
//        retrofitService.getFilms(getDefaultCategoryFromPreferences(), API.KEY, "ru-RU", page).enqueue(object : Callback<TmdbResultsDto> {
//            override fun onResponse(call: Call<TmdbResultsDto>, response: Response<TmdbResultsDto>) {
//                //При успехе мы вызываем метод передаем onSuccess и в этот коллбэк список фильмов
//                Timber.d("Get Films Success")
//                val list = Converter.convertApiListToDtoList(response.body()?.tmdbFilms)
//                //Кладем фильмы в бд
//                Completable.fromSingle<List<Film>> {
//                    repo.putToDb(list)
//                }
//                    .subscribeOn(Schedulers.io())
//                    .subscribe()
//                progressBarState.onNext(false)
//            }
//
//            override fun onFailure(call: Call<TmdbResultsDto>, t: Throwable) {
//                Timber.d("Get Films Failure")
//                //В случае провала выключаем ProgressBar
//                progressBarState.onNext(false)
//            }
//        })
//    }

    // Получаем результат запроса поиска
    fun getSearchResultFromApi(search: String, page: Int = 1): Observable<List<Film>> = retrofitService.getFilmFromSearch(API.KEY, "ru-RU", search, page)
        .map {
            Timber.d("tmdbFilms.size = " + it.tmdbFilms.size)
            Converter.convertApiListToDtoList(it.tmdbFilms)
        }

    // Обновление rview исходными значениями при очистки поля поиска фильмов
    fun recallData(){
        Completable.fromSingle<List<Film>> {
            repo.putToDb(repo.getAllFromDBAsList())
        }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun getFilmsFromDB(): Observable<List<Film>> = repo.getAllFromDB()

    fun getDBSize(): Int = repo.getSize()

    fun clearCache()
    {
        repo.clearCache()
    }

    fun getFavorites() : List<Film>
    {
        return repo.favoritesFilms
    }

    fun updateFavorites(srcList: List<Film>): List<Film>
    {
        val dscList = srcList
        // сверямеся со списком избранных если фильм в списке актуализируем признак isInFavorites
        var favFilms = repo.favoritesFilms
        dscList.forEach {film->
            film.isInFavorites = false
            if(favFilms.isNotEmpty())
                favFilms.forEach {favFilm->
                    if(film.id == favFilm.id) film.isInFavorites = true
                }
        }
        return dscList
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


