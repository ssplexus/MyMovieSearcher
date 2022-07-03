package ru.ssnexus.mymoviesearcher.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    //Данные текущей страницы с сервера
    private var currentPageData: List<Film>? = null

    //Всего страниц
    private var totalPages : Int = 0
    private val apiCallback : ApiCallback?

    //Отслеживание базы данных
    val filmsListLiveData: LiveData<List<Film>>
    //Отслеживание данных состояния прогрессбара
    val showProgressBar: MutableLiveData<Boolean> = MutableLiveData()
    //Отслеживание ошибок соединения
    val errorEvent = SingleLiveEvent<String>()

    //Инициализируем интерактор
    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
        filmsListLiveData = interactor.getFilmsFromDB()

        apiCallback = object : ApiCallback {
            override fun onSuccess(resultsDto: TmdbResultsDto?) {
                showProgressBar.postValue(false)
                if(resultsDto != null)
                {
                    currentPage = resultsDto.page
                    totalPages = resultsDto.totalPages
                    currentPageData = Converter.convertApiListToDtoList(resultsDto.tmdbFilms)

                    updatePageData()
                }
                else
                {
                    Timber.d("ResultsDTO is null")
                    currentPage = 0
                    totalPages = 0
                }
            }

            override fun onFailure() {
                Timber.d("Get data error!")
                errorEvent.postValue("Get data error!")
                showProgressBar.postValue(false)
            }
        }
    }

    //Получить данные 1 стрницы
    fun updateFilms() {
        if (apiCallback != null) {
                showProgressBar.postValue(true)
                interactor.getFilmsFromApi(1, apiCallback)
        }
    }

    //Получить фильмы
    fun getFilms() {
        if (apiCallback != null) {
            showProgressBar.postValue(true)
            interactor.getFilmsFromApi(getPage(), apiCallback)
        }
    }

    //Получить следующую страницу
    fun getPage() : Int {
        var page : Int = currentPage + 1
        if(page !in 1..totalPages) page = 1
        return page
    }

    //Добавление данных в базу и RecyclerView
    fun updatePageData() {
        currentPageData?.let { interactor.addFilmsToDB(it) }
    }

    interface ApiCallback {
        fun onSuccess(films: TmdbResultsDto?)
        fun onFailure()
    }
}