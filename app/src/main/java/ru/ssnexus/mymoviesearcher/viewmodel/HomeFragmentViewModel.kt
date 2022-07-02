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

    private var currentPageData: List<Film>? = null

    private var totalPages : Int = 0
    private val apiCallback : ApiCallback?

    val filmsListLiveData: LiveData<List<Film>>
    val showProgressBar: MutableLiveData<Boolean> = MutableLiveData()

    //Инициализируем интерактор
    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
        filmsListLiveData = interactor.getFilmsFromDB()
//        if(interactor.repo.getAllFromDB().value == null)
//                    Timber.d("Get dB")
//        interactor.repo.getAllFromDB().value?.let {
//            Timber.d("Get dB")
//            if(it.isEmpty()) updateFilms()
//        }

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
                Timber.d("Get films error! ")
                showProgressBar.postValue(false)
            }
        }
    }

    fun updateFilms() {
        if (apiCallback != null) {
                showProgressBar.postValue(true)
                interactor.getFilmsFromApi(1, apiCallback)
        }
    }

    fun getFilms() {
        if (apiCallback != null) {
            showProgressBar.postValue(true)
            interactor.getFilmsFromApi(getPage(), apiCallback)
        }
    }

    fun getPage() : Int {
        var page : Int = currentPage + 1
        if(page !in 1..totalPages) page = 1
        return page
    }

    fun updatePageData() {
        currentPageData?.let { interactor.addFilmsToDB(it) }
    }

    interface ApiCallback {
        fun onSuccess(films: TmdbResultsDto?)
        fun onFailure()
    }
}