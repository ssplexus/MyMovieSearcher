package ru.ssnexus.database_module

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [DatabaseModule::class]
)
interface DatabaseComponent: DatabaseProvider
