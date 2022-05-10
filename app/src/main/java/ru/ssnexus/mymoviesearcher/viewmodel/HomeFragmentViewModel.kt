package ru.ssnexus.mymoviesearcher.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.ssnexus.mymoviesearcher.App
import ru.ssnexus.mymoviesearcher.data.TmdbResultsDto
import ru.ssnexus.mymoviesearcher.domain.Film
import ru.ssnexus.mymoviesearcher.domain.Interactor
import ru.ssnexus.mymoviesearcher.utils.Converter
import timber.log.Timber

class HomeFragmentViewModel : ViewModel() {
    //Позиция скролла при переходе между страницами
    var scrollToPosition: Int = 0
    var currentPage : Int = 0
    //Сколько фильмов на странице
    var totalPageResults : Int = 0

    private var totalPages : Int = 0
    private val apiCallback : ApiCallback?

    val filmsListLiveData = MutableLiveData<List<Film>>()
    //Инициализируем интерактор
    private var interactor: Interactor = App.instance.interactor

    init {
        apiCallback = object : ApiCallback {
            override fun onSuccess(resultsDto: TmdbResultsDto?) {

                if(resultsDto != null)
                {
                    currentPage = resultsDto.page
                    totalPageResults = resultsDto.tmdbFilms.size
                    totalPages = resultsDto.totalPages
                    var films = Converter.convertApiListToDtoList(resultsDto.tmdbFilms)

                    // сверямеся со списком избранных если фильм в списке актуализируем признак isInFavorites
                    var favFilms = interactor.repo.favoritesFilms
                    if(favFilms.isNotEmpty())
                        for(i in 0 until films.size) {
                            for(favFilm in favFilms)
                            {
                                if(films[i].id == favFilm.id) films[i].isInFavorites = true
                            }
                        }
                    filmsListLiveData.postValue(films)
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

    interface ApiCallback {
        fun onSuccess(films: TmdbResultsDto?)
        fun onFailure()
    }
}