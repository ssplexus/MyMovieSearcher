package ru.ssnexus.mymoviesearcher.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.ssnexus.mymoviesearcher.App
import ru.ssnexus.mymoviesearcher.domain.Film
import ru.ssnexus.mymoviesearcher.domain.Interactor
import javax.inject.Inject

class FavoritesFragmentViewModel : ViewModel(){

    val favFilmsListLiveData = MutableLiveData<List<Film>>()

    //Инициализируем интерактор
    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
    }

    fun getData()
    {
        favFilmsListLiveData.postValue(interactor.getFavorites())
    }

    fun removeFromFavorites(film : Film)
    {
        interactor.removeFromFavorites(film)
    }

}