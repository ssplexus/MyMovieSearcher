package ru.ssnexus.mymoviesearcher.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.ssnexus.mymoviesearcher.App
import ru.ssnexus.mymoviesearcher.domain.Film
import ru.ssnexus.mymoviesearcher.domain.Interactor

class FavoritesFragmentViewHolder : ViewModel() {
    //Инициализируем интерактор
    private var interactor: Interactor = App.instance.interactor

    fun getData() = interactor.getFavorites()

}