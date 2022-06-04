package ru.ssnexus.mymoviesearcher.di

import dagger.Component
import ru.ssnexus.mymoviesearcher.di.modules.DatabaseModule
import ru.ssnexus.mymoviesearcher.di.modules.DomainModule
import ru.ssnexus.mymoviesearcher.di.modules.RemoteModule
import ru.ssnexus.mymoviesearcher.viewmodel.DetailsFragmentViewModel
import ru.ssnexus.mymoviesearcher.viewmodel.FavoritesFragmentViewModel
import ru.ssnexus.mymoviesearcher.viewmodel.HomeFragmentViewModel
import ru.ssnexus.mymoviesearcher.viewmodel.SettingsFragmentViewModel
import javax.inject.Singleton

@Singleton
@Component(
    //Внедряем все модули, нужные для этого компонента
    modules = [
        RemoteModule::class,
        DatabaseModule::class,
        DomainModule::class
    ]
)
interface AppComponent {
    //метод для того, чтобы появилась внедрять зависимости в HomeFragmentViewModel
    fun inject(homeFragmentViewModel: HomeFragmentViewModel)
    fun inject(favoritesFragmentViewModel: FavoritesFragmentViewModel)
    fun inject(detailsFragmentViewModel: DetailsFragmentViewModel)
    fun inject(settingsFragmentViewModel: SettingsFragmentViewModel)
}