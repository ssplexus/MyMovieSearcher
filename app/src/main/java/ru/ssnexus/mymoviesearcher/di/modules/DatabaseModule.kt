package ru.ssnexus.mymoviesearcher.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.ssnexus.mymoviesearcher.data.MainRepository
import ru.ssnexus.mymoviesearcher.data.db.DatabaseHelper
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabaseHelper(context: Context) = DatabaseHelper(context)

    @Provides
    @Singleton
    fun provideRepository(databaseHelper: DatabaseHelper) = MainRepository(databaseHelper)
}