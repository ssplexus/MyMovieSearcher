package ru.ssnexus.mymoviesearcher.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.*
import ru.ssnexus.database_module.data.entity.Film
import ru.ssnexus.mymoviesearcher.App
import ru.ssnexus.mymoviesearcher.domain.Interactor
import ru.ssnexus.mymoviesearcher.view.notifications.NotificationConstants
import ru.ssnexus.mymoviesearcher.view.notifications.NotificationHelper
import javax.inject.Inject

class ReminderBroadcast : BroadcastReceiver() {

    //Инициализируем интерактор
    @Inject
    lateinit var interactor: Interactor

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        App.instance.dagger.inject(this)
    }

    override fun onReceive(context: Context?, intent: Intent?) {

        val bundle = intent?.getBundleExtra(NotificationConstants.FILM_BUNDLE_KEY)
        val film: Film = bundle?.get(NotificationConstants.FILM_KEY) as Film

        NotificationHelper.createNotification(context!!, film)
        MainScope().launch {
            scope.async {
                interactor.updateFilmWatchLaterState(film)
            }
        }

    }
}
