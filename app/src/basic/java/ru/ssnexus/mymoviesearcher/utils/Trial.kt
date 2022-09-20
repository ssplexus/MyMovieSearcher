package ru.ssnexus.mymoviesearcher.utils

import android.content.Context
import ru.ssnexus.mymoviesearcher.data.preferences.PreferenceProvider
import ru.ssnexus.mymoviesearcher.domain.Interactor
import ru.ssnexus.mymoviesearcher.view.notifications.NotificationHelper
import java.util.concurrent.TimeUnit

class Trial() {
    companion object {
        private const val TRIAL_DAYS = 14

        fun getTrial(interactor: Interactor): Boolean {
            var time = System.currentTimeMillis() - interactor.getFirstLaunchTime()
            time = TimeUnit.MILLISECONDS.toDays(time)
            return time <= TRIAL_DAYS
        }
    }
}