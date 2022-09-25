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

    // Получаем результат запроса поиска
    fun getSearchResultFromApi(search: String, page: Int = 1): Observable<List<Film>> = retrofitService.getFilmFromSearch(API.KEY, "ru-RU", search, page)
        .map {
            Converter.convertApiListToDtoList(it.tmdbFilms)
        }

    fun getFilmFromApi(id: Int): Observable<Film> = retrofitService.getFilm(id, API.KEY, "ru-RU")
        .map {
            Converter.convertApiToDto(it)
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

    // Получить список избранных
    fun getFavorites() : List<Film>
    {
        return repo.getFavorites()
    }

    // Получить состояние карточки фильма (в избранном или нет)
    fun getFilmFavState(film: Film) : Int = repo.getFilmFavStateById(film.id)

    // Обновить состояние "в избранном" карточки фильма
    fun updateFilmFavState(film : Film){
        repo.updateFilmFavStateById(film.id)
    }

    // Получить список "посмотроеть позже"
    fun getWatchLater() : List<Film>
    {
        return repo.getWatchLater()
    }

    // Получить состояние "посмотреть позже" карточки фильма
    fun getFilmWatchLaterState(film: Film) : Int = repo.getWatchLaterStateById(film.id)

    // Обновить состояние "посмотреть позже" карточки фильма
    fun updateFilmWatchLaterState(film : Film){
        repo.updateWatchLaterStateById(film.id)
    }

    //Метод для сохранения настроек
    fun saveDefaultCategoryToPreferences(category: String) {
        preferences.saveDefaultCategory(category)
    }
    //Метод для получения настроек
    fun getDefaultCategoryFromPreferences() = preferences.getDefaultCategory()

    //Получить время первого запуска
    fun getFirstLaunchTime() = preferences.getFirstLaunchTime()

}


