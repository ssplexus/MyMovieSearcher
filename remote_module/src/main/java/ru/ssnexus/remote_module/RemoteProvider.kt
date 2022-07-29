package ru.ssnexus.remote_module

interface RemoteProvider {
    fun provideRemote(): TmdbApi
}