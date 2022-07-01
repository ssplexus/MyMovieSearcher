package ru.ssnexus.mymoviesearcher.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.ssnexus.mymoviesearcher.data.DAO.FilmDao
import ru.ssnexus.mymoviesearcher.data.entity.Film

@Database(entities = [Film::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun filmDao(): FilmDao
}