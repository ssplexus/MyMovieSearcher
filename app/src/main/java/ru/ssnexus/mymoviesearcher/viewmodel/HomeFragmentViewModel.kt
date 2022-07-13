package ru.ssnexus.mymoviesearcher.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import ru.ssnexus.mymoviesearcher.App
import ru.ssnexus.mymoviesearcher.data.entity.TmdbResultsDto
import ru.ssnexus.mymoviesearcher.data.entity.Film
import ru.ssnexus.mymoviesearcher.domain.Interactor
import ru.ssnexus.mymoviesearcher.utils.Converter
import timber.log.Timber
import java.util.concurrent.Executors
import javax.inject.Inject

class HomeFragmentViewModel : ViewModel(){

    var currentPage : Int = 0

    //Отслеживание базы данных
    val filmsListData: Flow<List<Film>>
    //Отслеживание данных состояния прогрессбара
    val showProgressBar: Channel<Boolean>

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