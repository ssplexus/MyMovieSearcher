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

    //Отслеживание базы данных
    val filmsListData: Observable<List<Film>>
    //Отслеживание данных состояния прогрессбара
    val showProgressBar: BehaviorSubject<Boolean>

    //Инициализируем интерактор
    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
        showProgressBar = interactor.progressBarState
        filmsListData = interactor.getFilmsFromDB()
        updateFilms()
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
}