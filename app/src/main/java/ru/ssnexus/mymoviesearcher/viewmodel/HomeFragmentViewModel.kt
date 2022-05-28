package ru.ssnexus.mymoviesearcher.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.ssnexus.mymoviesearcher.App
import ru.ssnexus.mymoviesearcher.data.TmdbResultsDto
import ru.ssnexus.mymoviesearcher.domain.Film
import ru.ssnexus.mymoviesearcher.domain.Interactor
import ru.ssnexus.mymoviesearcher.utils.Converter
import timber.log.Timber
import javax.inject.Inject

class HomeFragmentViewModel : ViewModel(){
    //Позиция скролла при переходе между страницами
    var scrollToPosition: Int = 0
    var currentPage : Int = 0
    //Сколько фильмов на странице
    var totalPageResults : Int = 0

    private var currentPageData: List<Film>? = null

    private var totalPages : Int = 0
    private val apiCallback : ApiCallback?

    val filmsListLiveData = MutableLiveData<List<Film>>()

    //Инициализируем интерактор
    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
        apiCallback = object : ApiCallback {
            override fun onSuccess(resultsDto: TmdbResultsDto?) {

                if(resultsDto != null)
                {
                    currentPage = resultsDto.page
                    totalPageResults = resultsDto.tmdbFilms.size
                    totalPages = resultsDto.totalPages
                    currentPageData = Converter.convertApiListToDtoList(resultsDto.tmdbFilms)

                    updatePageData()

                }
                else
                {
                    Timber.d("ResultsDTO is null")
                    currentPage = 0
                    totalPageResults = 0
                    totalPages = 0
                }

            }

            override fun onFailure() {
                Timber.d("Get films error!")
            }
        }

        //Возвращаемся на текущую страницу
        if(interactor.getLastSelectedPage() == 0) interactor.getFilmsFromApi(1 , apiCallback)
        else{
            currentPage = interactor.getLastSelectedPage()
            interactor.lastSelectedPageReset()
            interactor.getFilmsFromApi(currentPage , apiCallback)
        }
    }

    //Получить данные следующей страницы
    fun getNextPageData(){
        if(currentPage + 1 in 2..totalPages) {
            apiCallback?.let { interactor.getFilmsFromApi(currentPage + 1, it) }
            scrollToPosition = 0;
        }
    }

    //Получить данные предыдущей страницы
    fun getPrevPageData(){
        if(currentPage - 1 in 1 until totalPages) {
            apiCallback?.let { interactor.getFilmsFromApi(currentPage - 1, it) }
            scrollToPosition = 19
        }
    }

    fun updatePageData() {
        if(currentPageData == null) return
        // сверямеся со списком избранных если фильм в списке актуализируем признак isInFavorites
        var favFilms = interactor.repo.favoritesFilms
        currentPageData?.forEach {film->
            film.isInFavorites = false
            if(favFilms.isNotEmpty())
                favFilms.forEach {favFilm->
                    if(film.id == favFilm.id) film.isInFavorites = true
            }
        }
        filmsListLiveData.postValue(currentPageData)
    }

    interface ApiCallback {
        fun onSuccess(films: TmdbResultsDto?)
        fun onFailure()
    }
}