package ru.ssnexus.mymoviesearcher

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import ru.ssnexus.mymoviesearcher.fragments.*
import ru.ssnexus.mymoviesearcher.model.Film

class MainActivity : AppCompatActivity() {

    val db : MoviesDatabase = MoviesDatabase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initDB()
        initNavigation()

        //Запускаем фрагмент при старте
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_placeholder, HomeFragment())
            .addToBackStack(null)
            .commit()

    }

    fun initDB()
    {
        db.setDB(listOf(
            Film(0,"Black Phone", R.drawable.black_phone, "After being abducted by a child killer and locked in a soundproof basement, a 13-year-old boy starts receiving calls on a disconnected phone from the killer's previous victims.", 7.1f),
            Film(1,"Hannibal", R.drawable.hannibal,"Living in exile, Dr. Hannibal Lecter tries to reconnect with now disgraced F.B.I. Agent Clarice Starling, and finds himself a target for revenge from a powerful victim.", 9.8f),
            Film(2,"Horse Whisperer", R.drawable.horse_whisperer,"The mother of a severely traumatized daughter enlists the aid of a unique horse trainer to help the girl's equally injured horse.", 9.1f),
            Film(3,"Hostel", R.drawable.hostel,"Three backpackers head to a Slovak city that promises to meet their hedonistic expectations, with no idea of the hell that awaits them.", 5.4f),
            Film(4,"Lost City", R.drawable.lost_city, "A reclusive romance novelist on a book tour with her cover model gets swept up in a kidnapping attempt that lands them both in a cutthroat jungle adventure.", 6.3f),
            Film(5,"Saw",R.drawable.sawx,"As a deadly battle rages over Jigsaw's brutal legacy, a group of Jigsaw survivors gathers to seek the support of self-help guru and fellow survivor Bobby Dagen, a man whose own dark secrets unleash a new wave of terror.", 7.5f),
            Film(6,"When Will I Be Loved", R.drawable.when_will_i_be_loved, "Feeling undervalued by her boyfriend, a young woman begins to explore her sexuality with other people.", 8.3f)
            )
        )
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
        /*
        topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.settings -> {
                    Toast.makeText(this, R.string.btn_settings, Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        topAppBar.setNavigationOnClickListener {
            Toast.makeText(this, "Когда-нибудь здесь будет навигация...", Toast.LENGTH_SHORT).show()
        }*/

        bottom_navigation.setOnNavigationItemSelectedListener {

            val snackbar = Snackbar.make(layout_main, "", Snackbar.LENGTH_SHORT)

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
                    val fragment = checkFragmentExistence(tag)
                    changeFragment( fragment?: WatchLaterFragment(), tag)
                    true
                }
                R.id.selections -> {
                    val tag = "selections"
                    val fragment = checkFragmentExistence(tag)
                    changeFragment( fragment?: SelectionsFragment(), tag)
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
