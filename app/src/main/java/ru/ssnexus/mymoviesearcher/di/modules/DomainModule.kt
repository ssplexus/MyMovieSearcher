package ru.ssnexus.mymoviesearcher.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.ssnexus.mymoviesearcher.data.MainRepository
import ru.ssnexus.mymoviesearcher.data.preferences.PreferenceProvider
import ru.ssnexus.mymoviesearcher.data.TmdbApi
import ru.ssnexus.mymoviesearcher.domain.Interactor
import javax.inject.Singleton

@Module
class DomainModule (val context: Context) {

    //Нам нужно контекст как-то провайдить, поэтому создаем такой метод
    @Provides
    fun provideContext() = context

    @Singleton
    @Provides
    //Создаем экземпляр SharedPreferences
    fun providePreferences(context: Context) = PreferenceProvider(context)

    @Singleton
    @Provides
    fun provideInteractor(repository: MainRepository, tmdbApi: TmdbApi, preferenceProvider: PreferenceProvider):Interactor {
        return Interactor(repo = repository, retrofitService = tmdbApi, preferences = preferenceProvider)
    }

}

