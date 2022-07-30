package ru.ssnexus.database_module.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.ssnexus.database_module.data.DAO.FilmDao
import ru.ssnexus.database_module.data.entity.Film

@Database(entities = [Film::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun filmDao(): FilmDao
}