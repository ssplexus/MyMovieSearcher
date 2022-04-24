package ru.ssnexus.mymoviesearcher.fragments

import android.os.Bundle
import android.transition.Fade
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home.*
import ru.ssnexus.mymoviesearcher.AnimationHelper
import ru.ssnexus.mymoviesearcher.MainActivity
import ru.ssnexus.mymoviesearcher.adapter.FilmListRecyclerAdapter
import ru.ssnexus.mymoviesearcher.databinding.FragmentFavoritesBinding
import ru.ssnexus.mymoviesearcher.helper.ItemTouchHelperCallback
import ru.ssnexus.mymoviesearcher.model.Film
import ru.ssnexus.mymoviesearcher.model.decoration.TopSpacingItemDecoration


class FavoritesFragment : Fragment() {
    init {
        exitTransition = Fade().apply { duration = 800;mode = Fade.MODE_OUT }
        reenterTransition = Fade().apply { duration = 800; }

    }

    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var filmsAdapter: FilmListRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_init((requireActivity() as MainActivity).db.getFavorites())
        AnimationHelper.performFragmentCircularRevealAnimation(binding.root, requireActivity(), 2)
    }

    fun rv_init(db : List<Film>){
        //находим наш RV
        binding.favoritesRecycler.apply {

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
