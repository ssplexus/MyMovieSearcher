package ru.ssnexus.database_module

import ru.ssnexus.database_module.data.MainRepository

interface DatabaseProvider {
    fun provideDatabase(): MainRepository
}