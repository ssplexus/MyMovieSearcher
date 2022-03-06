package ru.ssnexus.mymoviesearcher

import android.content.Intent
import ru.ssnexus.mymoviesearcher.adapter.FilmListRecyclerAdapter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import ru.ssnexus.mymoviesearcher.fragments.FavoritesFragment
import ru.ssnexus.mymoviesearcher.fragments.HomeFragment
import ru.ssnexus.mymoviesearcher.helper.ItemTouchHelperCallback
import ru.ssnexus.mymoviesearcher.model.Film
import ru.ssnexus.mymoviesearcher.model.Item
import ru.ssnexus.mymoviesearcher.model.decoration.TopSpacingItemDecoration

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
            Film(0,"Black Phone", R.drawable.black_phone, "After being abducted by a child killer and locked in a soundproof basement, a 13-year-old boy starts receiving calls on a disconnected phone from the killer's previous victims."),
            Film(1,"Hannibal", R.drawable.hannibal,"Living in exile, Dr. Hannibal Lecter tries to reconnect with now disgraced F.B.I. Agent Clarice Starling, and finds himself a target for revenge from a powerful victim."),
            Film(2,"Horse Whisperer", R.drawable.horse_whisperer,"The mother of a severely traumatized daughter enlists the aid of a unique horse trainer to help the girl's equally injured horse."),
            Film(3,"Hostel", R.drawable.hostel,"Three backpackers head to a Slovak city that promises to meet their hedonistic expectations, with no idea of the hell that awaits them."),
            Film(4,"Lost City", R.drawable.lost_city, "A reclusive romance novelist on a book tour with her cover model gets swept up in a kidnapping attempt that lands them both in a cutthroat jungle adventure."),
            Film(5,"Saw",R.drawable.sawx,"As a deadly battle rages over Jigsaw's brutal legacy, a group of Jigsaw survivors gathers to seek the support of self-help guru and fellow survivor Bobby Dagen, a man whose own dark secrets unleash a new wave of terror."),
            Film(6,"When Will I Be Loved", R.drawable.when_will_i_be_loved, "Feeling undervalued by her boyfriend, a young woman begins to explore her sexuality with other people.")
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

    fun initNavigation()
    {
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
        }
        bottom_navigation.setOnNavigationItemSelectedListener {

            val snackbar = Snackbar.make(layout_main, "", Snackbar.LENGTH_SHORT)

            when (it.itemId) {
                R.id.favorites -> {
                    //Запускаем фрагмент "избранное"
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_placeholder, FavoritesFragment())
                        .addToBackStack(null)
                        .commit()
                    true
                }
                R.id.watch_later -> {
                    //Toast.makeText(this, "Посмотреть позже", Toast.LENGTH_SHORT).show()
                    snackbar.setText(R.string.btn_later)
                    snackbar.show()
                    true
                }
                R.id.selections -> {
                    //Toast.makeText(this, "Подборки", Toast.LENGTH_SHORT).show()
                    snackbar.setText(R.string.btn_compile)
                    snackbar.show()
                    true
                }
                else -> false
            }
        }
    }

}
