package ru.ssnexus.mymoviesearcher

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.res.Configuration
import android.os.Build
import ru.ssnexus.mymoviesearcher.di.AppComponent
import ru.ssnexus.mymoviesearcher.di.DaggerAppComponent
import ru.ssnexus.mymoviesearcher.di.modules.DomainModule
import ru.ssnexus.remote_module.DaggerRemoteComponent
import ru.ssnexus.database_module.DaggerDatabaseComponent
import ru.ssnexus.database_module.DatabaseModule
import ru.ssnexus.mymoviesearcher.view.notifications.NotificationConstants.CHANNEL_DESCRIPTION
import ru.ssnexus.mymoviesearcher.view.notifications.NotificationConstants.CHANNEL_ID
import ru.ssnexus.mymoviesearcher.view.notifications.NotificationConstants.CHANNEL_NAME
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

        createNotificationChannel()

        val databaseModule = DatabaseModule(this)
        val remoteProvider = DaggerRemoteComponent.create()
        val databaseProvider = DaggerDatabaseComponent.builder().databaseModule(databaseModule).build()
        //Создаем компонент
        dagger = DaggerAppComponent.builder()
            .remoteProvider(remoteProvider)
            .databaseProvider(databaseProvider)
            .domainModule(DomainModule(this))
            .build()

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

    fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Задаем имя, описание и важность канала
            //Создаем канал, передав в параметры его ID(строка), имя(строка), важность(константа)
            val mChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            //Отдельно задаем описание
            mChannel.description = CHANNEL_DESCRIPTION
            //Получаем доступ к менеджеру нотификаций
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            //Регистрируем канал
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    companion object {
        lateinit var instance: App
            private set
    }
}