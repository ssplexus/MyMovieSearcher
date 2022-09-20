package ru.ssnexus.mymoviesearcher.view

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ru.ssnexus.mymoviesearcher.App
import ru.ssnexus.mymoviesearcher.R
import ru.ssnexus.database_module.data.entity.Film
import ru.ssnexus.mymoviesearcher.databinding.ActivityMainBinding
import ru.ssnexus.mymoviesearcher.domain.Interactor
import ru.ssnexus.mymoviesearcher.receivers.ConnectionChecker
import ru.ssnexus.mymoviesearcher.utils.Trial
import ru.ssnexus.mymoviesearcher.view.fragments.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var interactor: Interactor

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

}
