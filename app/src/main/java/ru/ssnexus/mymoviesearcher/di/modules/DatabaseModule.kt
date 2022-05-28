package ru.ssnexus.mymoviesearcher.di.modules

import dagger.Module
import dagger.Provides
import ru.ssnexus.mymoviesearcher.data.MainRepository
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun provideRepository() = MainRepository()
}