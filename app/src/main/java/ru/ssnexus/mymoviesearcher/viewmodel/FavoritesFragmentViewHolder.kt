package ru.ssnexus.mymoviesearcher.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.koin.core.KoinComponent
import org.koin.core.inject
import ru.ssnexus.mymoviesearcher.domain.Film
import ru.ssnexus.mymoviesearcher.domain.Interactor

class FavoritesFragmentViewHolder : ViewModel(), KoinComponent {

    val favFilmsListLiveData = MutableLiveData<List<Film>>()

    //Инициализируем интерактор
    private val interactor: Interactor by inject()

    fun getData()
    {
        favFilmsListLiveData.postValue(interactor.getFavorites())
    }

    fun removeFromFavorites(film : Film)
    {
        interactor.removeFromFavorites(film)
    }
}