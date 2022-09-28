package ru.ssnexus.mymoviesearcher.view

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*
import ru.ssnexus.mymoviesearcher.App
import ru.ssnexus.mymoviesearcher.R
import ru.ssnexus.database_module.data.entity.Film
import ru.ssnexus.mymoviesearcher.databinding.ActivityMainBinding
import ru.ssnexus.mymoviesearcher.domain.Interactor
import ru.ssnexus.mymoviesearcher.receivers.ConnectionChecker
import ru.ssnexus.mymoviesearcher.utils.AutoDisposable
import ru.ssnexus.mymoviesearcher.utils.Trial
import ru.ssnexus.mymoviesearcher.utils.addTo
import ru.ssnexus.mymoviesearcher.view.customview.PromoView
import ru.ssnexus.mymoviesearcher.view.fragments.*
import timber.log.Timber
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var interactor: Interactor

    private val autoDisposable = AutoDisposable()
    private lateinit var receiver: BroadcastReceiver
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.dagger.inject(this)
        //Инициализируем объект
        binding = ActivityMainBinding.inflate(layoutInflater)
        //Передаем его в метод
        setContentView(binding.root)

        initNavigation()

        // Приёмник внешних событий
        receiver = ConnectionChecker()

        // Фильтр событий
        val filters = IntentFilter().apply {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_BATTERY_LOW)
            addAction(Intent.ACTION_BATTERY_OKAY)
        }

        // Регистрация приёмника
        registerReceiver(receiver, filters)

        //Промо
        startPromo()

        if(savedInstanceState == null)
        {
            val extras = intent.extras
            if(extras != null)
            {
                val film = extras.get(R.string.parcel_item_film.toString()) as Film
                launchDetailsFragment(film)
                return
            }
        }

        //Запускаем фрагмент при старте
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_placeholder, HomeFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Разрегистрируем приёмник
        unregisterReceiver(receiver)
    }

    fun launchDetailsFragment(film: Film) {
        //Создаем "посылку"
        val bundle = Bundle()
        //Кладем наш фильм в "посылку"
        bundle.putParcelable(R.string.parcel_item_film.toString(), film)
        //Кладем фрагмент с деталями в перменную
        val fragment = DetailsFragment()
        //Прикрепляем нашу "посылку" к фрагменту
        fragment.arguments = bundle

        //Запускаем фрагмент
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_placeholder, fragment)
            .addToBackStack(null)
            .commit()
    }

    fun initNavigation()
    {
        binding.bottomNavigation.setOnNavigationItemSelectedListener {

            when (it.itemId) {

                R.id.home -> {
                    val tag = "home"
                    val fragment = checkFragmentExistence(tag)
                    //В первом параметре, если фрагмент не найден и метод вернул null, то с помощью
                    //элвиса мы вызываем создание нового фрагмента
                    changeFragment( fragment?: HomeFragment(), tag)
                    true
                }
                R.id.favorites -> {
                    val tag = "favorites"
                    val fragment = checkFragmentExistence(tag)
                    changeFragment( fragment?: FavoritesFragment(), tag)
                    true
                }
                R.id.watch_later -> {
                    val tag = "watch_later"
                    if(!Trial.getTrial(interactor))
                        Toast.makeText(this, "Закончился пробный период!\nДоступно в Pro версии", Toast.LENGTH_SHORT).show()
                    else{
                        val fragment = checkFragmentExistence(tag)
                        changeFragment( fragment?: WatchLaterFragment(), tag)
                    }
                    true
                }
                R.id.selections -> {
                    val tag = "selections"
                    if(!Trial.getTrial(interactor))
                        Toast.makeText(this, "Закончился пробный период!\nДоступно в Pro версии", Toast.LENGTH_SHORT).show()
                    else
                    {
                        val fragment = checkFragmentExistence(tag)
                        changeFragment( fragment?: SelectionsFragment(), tag)
                    }
                    true
                }
                R.id.settings -> {
                    val tag = "settings"
                    val fragment = checkFragmentExistence(tag)
                    changeFragment( fragment?: SettingsFragment(), tag)
                    true
                }
                else -> false
            }
        }
    }

    //Ищем фрагмент по тегу, если он есть то возвращаем его, если нет, то null
    private fun checkFragmentExistence(tag: String): Fragment? = supportFragmentManager.findFragmentByTag(tag)

    private fun changeFragment(fragment: Fragment, tag: String) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_placeholder, fragment, tag)
            .addToBackStack(null)
            .commit()
    }

    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount == 1)
        {
            AlertDialog.Builder(this)
                .setTitle(R.string.is_exit)
                .setIcon(R.drawable.ic_round_menu_24)
                .setPositiveButton(R.string.yes) { _, _ ->
                    super.onBackPressed()
                    if(android.os.Build.VERSION.SDK_INT >= 21)
                    {
                        finishAndRemoveTask();
                    }
                    else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
                    {
                        finishAffinity();
                    } else{
                        finish()
                    }
                    System.exit(0);
                }
                .setNegativeButton(R.string.no) { _, _ ->

                }
                .show()
        }
        else
            super.onBackPressed()

    }

    private fun startPromo(){
        if (!App.instance.isPromoShown) {
            //Получаем доступ к Remote Config
            val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

            //Устанавливаем настройки
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build()
            firebaseRemoteConfig.setConfigSettingsAsync(configSettings)

            //Вызываем метод, который получит данные с сервера и вешаем слушатель
            firebaseRemoteConfig.fetch()
                .addOnCompleteListener {
                    //Если все получилось успешно
                    if (it.isSuccessful) {
                        //активируем последний полученный конфиг с сервера
                        firebaseRemoteConfig.activate()
                        //Получаем ссылку на фильм
                        val filmLink = firebaseRemoteConfig.getLong(PromoView.POSTER_ID_KEY)
                        val scope = CoroutineScope(Dispatchers.IO)

                        //Запрашиваем фильм на сервере по id и по результату запускаем постер
                        MainScope().launch {
                            scope.async {
                                interactor.getFilmFromApi(filmLink.toInt()).showPoster()
                            }
                        }
                     }
                }
        }
    }

    //Показ постера
    private fun showPromo(film: Film){
        //Если фильм есть
        if (film?.poster != null) {
            //Ставим флаг, что уже промо показали
            App.instance.isPromoShown = true
            //Включаем промо верстку
            binding.promoViewGroup.apply {
                //Делаем видимой
                visibility = View.VISIBLE
                //Анимируем появление
                animate()
                    .setDuration(PromoView.POSTER_ANIM_DURATION)
                    .alpha(PromoView.POSTER_ANIM_ALPHA)
                    .start()
                //Вызываем метод, который загрузит постер в ImageView
                film?.let { it.poster?.let {
                        poster -> setLinkForPoster(poster) }
                }

                //Кнопка по нажатии на которую проимсходит переход к фильму
                watchButton.setOnClickListener {
                    visibility = View.GONE
                    film?.let {
                        launchDetailsFragment(it)
                    }
                }
                //Кнопка для закрытия промо
                binding.closeButton.setOnClickListener {
                    visibility = View.GONE
                }
            }
        }
    }

    //Запуск показа постера
    private fun Observable<Film>.showPoster(){
        this.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = {
                },
                onNext = {
                    showPromo(it)
                }
            )
            .addTo(autoDisposable)
    }

}
