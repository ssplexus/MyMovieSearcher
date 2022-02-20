package ru.ssnexus.mymoviesearcher.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import ru.ssnexus.mymoviesearcher.MainActivity
import ru.ssnexus.mymoviesearcher.R
import ru.ssnexus.mymoviesearcher.adapter.FilmListRecyclerAdapter
import ru.ssnexus.mymoviesearcher.helper.ItemTouchHelperCallback
import ru.ssnexus.mymoviesearcher.model.Film
import ru.ssnexus.mymoviesearcher.model.Item
import ru.ssnexus.mymoviesearcher.model.decoration.TopSpacingItemDecoration


/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    private lateinit var filmsAdapter: FilmListRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        RV_Init(getMoviesDB())
    }

    fun getMoviesDB(): List<Film>
    {
        return listOf(
            Film(0,"Black Phone", R.drawable.black_phone, "After being abducted by a child killer and locked in a soundproof basement, a 13-year-old boy starts receiving calls on a disconnected phone from the killer's previous victims."),
            Film(1,"Hannibal", R.drawable.hannibal,"Living in exile, Dr. Hannibal Lecter tries to reconnect with now disgraced F.B.I. Agent Clarice Starling, and finds himself a target for revenge from a powerful victim."),
            Film(2,"Horse Whisperer", R.drawable.horse_whisperer,"The mother of a severely traumatized daughter enlists the aid of a unique horse trainer to help the girl's equally injured horse."),
            Film(3,"Hostel", R.drawable.hostel,"Three backpackers head to a Slovak city that promises to meet their hedonistic expectations, with no idea of the hell that awaits them."),
            Film(4,"Lost City", R.drawable.lost_city, "A reclusive romance novelist on a book tour with her cover model gets swept up in a kidnapping attempt that lands them both in a cutthroat jungle adventure."),
            Film(5,"Saw",R.drawable.sawx,"As a deadly battle rages over Jigsaw's brutal legacy, a group of Jigsaw survivors gathers to seek the support of self-help guru and fellow survivor Bobby Dagen, a man whose own dark secrets unleash a new wave of terror."),
            Film(6,"When Will I Be Loved", R.drawable.when_will_i_be_loved, "Feeling undervalued by her boyfriend, a young woman begins to explore her sexuality with other people.")
        )
    }

    fun RV_Init(db : List<Item>){
        //находим наш RV
        main_recycler.apply {

            filmsAdapter = FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener{
                override fun click(film: Film) {
                    (requireActivity() as MainActivity).launchDetailsFragment(film)
                }
            })

            //Присваиваем адаптер
            adapter = filmsAdapter
            //Присвои layoutmanager
            layoutManager = LinearLayoutManager(requireContext())
            //Применяем декоратор для отступов
            val decorator = TopSpacingItemDecoration(8)
            addItemDecoration(decorator)
        }
        //Кладем нашу БД в RV
        filmsAdapter.addItems(db)

        val itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(filmsAdapter))
        itemTouchHelper.attachToRecyclerView(main_recycler)
    }

}