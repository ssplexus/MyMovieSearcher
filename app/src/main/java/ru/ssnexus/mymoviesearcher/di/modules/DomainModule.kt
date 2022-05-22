package ru.ssnexus.mymoviesearcher.di.modules

import dagger.Module
import dagger.Provides
import ru.ssnexus.mymoviesearcher.data.MainRepository
import ru.ssnexus.mymoviesearcher.data.TmdbApi
import ru.ssnexus.mymoviesearcher.domain.Interactor
import javax.inject.Singleton

@Module
class DomainModule {
    @Singleton
    @Provides
    fun provideInteractor(repository: MainRepository, tmdbApi: TmdbApi) = Interactor(repo = repository, retrofitService = tmdbApi)
}

