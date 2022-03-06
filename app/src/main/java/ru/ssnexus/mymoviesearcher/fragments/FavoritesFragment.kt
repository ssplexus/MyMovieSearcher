package ru.ssnexus.mymoviesearcher.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_favorites.*
import kotlinx.android.synthetic.main.fragment_home.*
import ru.ssnexus.mymoviesearcher.MainActivity
import ru.ssnexus.mymoviesearcher.R
import ru.ssnexus.mymoviesearcher.adapter.FilmListRecyclerAdapter
import ru.ssnexus.mymoviesearcher.helper.ItemTouchHelperCallback
import ru.ssnexus.mymoviesearcher.model.Film
import ru.ssnexus.mymoviesearcher.model.decoration.TopSpacingItemDecoration


class FavoritesFragment : Fragment() {
    private lateinit var filmsAdapter: FilmListRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        RV_Init((requireActivity() as MainActivity).db.getFavorites())
    }

    fun RV_Init(db : List<Film>){
        if(db == null || db.isEmpty())
        {

            //Toast.makeText(requireContext(), "Empty list", Toast.LENGTH_SHORT).show()
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.is_empty_list)
                .setPositiveButton(R.string.ok) { _, _ ->
                    (requireActivity() as MainActivity).onBackPressed()
                }
                .show()
            return
        }
        //находим наш RV
        favorites_recycler.apply {

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
