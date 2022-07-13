package ru.ssnexus.mymoviesearcher.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import ru.ssnexus.mymoviesearcher.App
import ru.ssnexus.mymoviesearcher.data.entity.Film
import ru.ssnexus.mymoviesearcher.domain.Interactor
import java.net.URL
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class DetailsFragmentViewModel:ViewModel() {

    //Инициализируем интерактор
    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
    }
    fun addToFavorites(film : Film)
    {
        interactor.addToFavorites(film)
    }

    fun removeFromFavorites(film : Film)
    {
        interactor.removeFromFavorites(film)
    }

    fun checkInFavorites(film: Film)
    {
        var favFilms = interactor.repo.favoritesFilms

        film.isInFavorites = false
        if(favFilms.isNotEmpty())
            favFilms.forEach {favFilm->
                if(film.id == favFilm.id) film.isInFavorites = true
            }
    }

    suspend fun loadWallpaper(url: String): Bitmap {
        return suspendCoroutine {
            val url = URL(url)
            val bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            it.resume(bitmap)
        }
    }
}