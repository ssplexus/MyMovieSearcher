package ru.ssnexus.mymoviesearcher

import android.app.Application
import android.content.res.Configuration
import ru.ssnexus.mymoviesearcher.di.AppComponent
import ru.ssnexus.mymoviesearcher.di.DaggerAppComponent
import timber.log.Timber

class App : Application() {

    lateinit var dagger: AppComponent
    // Этот метод вызывается при старте приложения до того, как будут созданы другие компоненты приложения
    // Этот метод необязательно переопределять, но это самое хорошее место для инициализации глобальных объектов
    override fun onCreate() {
        super.onCreate()

        if(BuildConfig.DEBUG)
        {
            Timber.plant(Timber.DebugTree())
        }

        instance = this
        //Создаем компонент
        dagger = DaggerAppComponent.create()
    }

    // Вызывается при изменении конфигурации, например, поворот
    // Этот метод тоже не обязателен к предопределению
    override fun onConfigurationChanged ( newConfig : Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    // Этот метод вызывается, когда у системы остается мало оперативной памяти
    // и система хочет, чтобы запущенные приложения поумерили аппетиты
    // Переопределять необязательно
    override fun onLowMemory() {
        super.onLowMemory()
    }

    companion object {
        lateinit var instance: App
            private set
    }
}