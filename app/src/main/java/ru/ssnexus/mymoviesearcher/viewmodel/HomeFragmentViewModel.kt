package ru.ssnexus.mymoviesearcher.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.ssnexus.mymoviesearcher.App
import ru.ssnexus.mymoviesearcher.data.entity.TmdbResultsDto
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

    var currentCategory : String = ""

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

                    //Кладем фильмы в бд
                    currentPageData?.forEach {
                        interactor.repo.putToDb(film = it)
                    }

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
                Timber.d("Get films error! " + interactor.getFilmsFromDB().size)
                filmsListLiveData.postValue(interactor.getFilmsFromDB())
            }
        }

        getFilms(0)
    }

    fun getFilms(direction : Int) {
        if (apiCallback != null) {
            interactor.getFilmsFromApi(getPage(direction), apiCallback)
        }
    }

    fun getPage(direction: Int) : Int {
        var page : Int = currentPage + 1 * direction
        //Если поменялась категория то начинаем с первой страницы
        if(!currentCategory.equals(interactor.getDefaultCategoryFromPreferences())) {
            currentCategory = interactor.getDefaultCategoryFromPreferences()
            page = 1
        }
        if(page !in 1..totalPages) page = 1

        Timber.d("Direction " + direction)
        if(direction >= 0 ) scrollToPosition = 0
        else if (currentPage > 1) scrollToPosition = totalPageResults - 1

        return page
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