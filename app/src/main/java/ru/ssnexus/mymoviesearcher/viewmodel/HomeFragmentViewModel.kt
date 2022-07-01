package ru.ssnexus.mymoviesearcher.viewmodel

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

    var isRefresh : Boolean = false
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
                Executors.newSingleThreadExecutor().execute {
                    filmsListLiveData.postValue(interactor.getFilmsFromDB())
                }
            }
        }
        currentCategory = interactor.getDefaultCategoryFromPreferences()
    }

    fun updateFilms() {
        if (apiCallback != null) {
                interactor.getFilmsFromApi(1, apiCallback)
        }
    }

    fun getFilms() {
        if (apiCallback != null) {
            interactor.getFilmsFromApi(getPage(), apiCallback)
        }
    }

    fun getPage() : Int {
        var page : Int = currentPage + 1
        if(page !in 1..totalPages) page = 1
        return page
    }

    fun setCachedData() {
        Executors.newSingleThreadExecutor().execute {
            val cache = interactor.getFilmsFromDB()
            if (!cache.isEmpty()) {
                isRefresh = false
                interactor.updateFavorites(cache)
                filmsListLiveData.postValue(cache)
            }
            else {
                isRefresh = true
                updateFilms()
            }
        }
    }

    fun clearCache()
    {
        Executors.newSingleThreadExecutor().execute {
            interactor.repo.clearCache()
        }
    }

    fun updatePageData() {
        Executors.newSingleThreadExecutor().execute {
            //Кладем фильмы в бд
            currentPageData?.let {
                interactor.repo.putToDb(it)
            }
            val cache = mutableListOf<Film>()
            cache.addAll(interactor.repo.getAllFromDB())

            interactor.updateFavorites(cache)
            filmsListLiveData.postValue(cache)
        }
    }

    interface ApiCallback {
        fun onSuccess(films: TmdbResultsDto?)
        fun onFailure()
    }
}