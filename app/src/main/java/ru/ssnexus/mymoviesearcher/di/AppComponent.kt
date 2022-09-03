package ru.ssnexus.mymoviesearcher.di

import dagger.Component
import ru.ssnexus.database_module.DatabaseModule
import ru.ssnexus.database_module.DatabaseProvider
import ru.ssnexus.mymoviesearcher.di.modules.DomainModule
import ru.ssnexus.mymoviesearcher.receivers.ReminderBroadcast
import ru.ssnexus.mymoviesearcher.view.MainActivity
import ru.ssnexus.mymoviesearcher.viewmodel.*
import ru.ssnexus.remote_module.RemoteProvider
import javax.inject.Singleton

@Singleton
@Component(
    //Внедряем все модули, нужные для этого компонента
    dependencies = [RemoteProvider::class, DatabaseProvider::class],
    modules = [
        DomainModule::class
    ]
)
interface AppComponent {

    fun inject(homeFragmentViewModel: HomeFragmentViewModel)
    fun inject(favoritesFragmentViewModel: FavoritesFragmentViewModel)
    fun inject(detailsFragmentViewModel: DetailsFragmentViewModel)
    fun inject(settingsFragmentViewModel: SettingsFragmentViewModel)
    fun inject(watchLaterFragmentViewModel: WatchLaterFragmentViewModel)
    fun inject(reminderBroadcast: ReminderBroadcast)
    fun inject(mainActivity: MainActivity)
}