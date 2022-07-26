package ru.ssnexus.mymoviesearcher.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.channels.Channel
import ru.ssnexus.mymoviesearcher.App
import ru.ssnexus.mymoviesearcher.data.entity.Film
import ru.ssnexus.mymoviesearcher.domain.Interactor
import timber.log.Timber
import javax.inject.Inject

class HomeFragmentViewModel : ViewModel(){

    var currentPage : Int = 0
    var currentSearchPage : Int = 0
    private var searchMode : Boolean = false

    //Отслеживание базы данных
    var filmsListData: Observable<List<Film>>
    //Отслеживание данных состояния прогрессбара
    val showProgressBar: BehaviorSubject<Boolean>

    //Инициализируем интерактор
    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
        showProgressBar = interactor.progressBarState
        filmsListData = interactor.getFilmsFromDB()
    }

    //Получить данные 1 стрницы
    fun updateFilms() {
        currentPage = 1
        Timber.d("Current page = " + currentPage)
        interactor.getFilmsFromApi(currentPage)
    }

    //Получить фильмы
    fun getFilms() {
        interactor.getFilmsFromApi(++currentPage)
        Timber.d("Current page = " + currentPage)
    }

    //Поиск фильмов
    fun getSearchResult(search: String):Observable<List<Film>> {
        Timber.d("Debug: getSearchResult " + search + " on page " + "${currentSearchPage + 1}")
        return interactor.getSearchResultFromApi(search, ++currentSearchPage)
    }

    fun setSearch(mode: Boolean = true){
        Timber.d("setSearch = " + mode)
        if(mode) currentSearchPage = 0
        else interactor.recallData()
        searchMode = mode

    }

    fun getSearch(): Boolean = searchMode
}