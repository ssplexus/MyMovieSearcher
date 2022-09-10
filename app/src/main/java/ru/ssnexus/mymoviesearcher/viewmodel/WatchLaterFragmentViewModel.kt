package ru.ssnexus.mymoviesearcher.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.ssnexus.database_module.data.entity.Film
import ru.ssnexus.mymoviesearcher.App
import ru.ssnexus.mymoviesearcher.domain.Interactor
import javax.inject.Inject

class WatchLaterFragmentViewModel :ViewModel(){
    val watchLaterFilmsListLiveData = MutableLiveData<List<Film>>()

    //Инициализируем интерактор
    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
    }

    fun getData()
    {
       watchLaterFilmsListLiveData.postValue(interactor.getWatchLater())
    }

    fun updateFilmState(film : Film){
       interactor.updateFilmWatchLaterState(film)
    }
}