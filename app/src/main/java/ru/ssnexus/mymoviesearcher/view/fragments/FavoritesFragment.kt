package ru.ssnexus.mymoviesearcher.view.fragments

import android.os.Bundle
import android.transition.Fade
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.ssnexus.mymoviesearcher.databinding.FragmentFavoritesBinding
import ru.ssnexus.database_module.data.entity.Film
import ru.ssnexus.mymoviesearcher.utils.AnimationHelper
import ru.ssnexus.mymoviesearcher.utils.ItemTouchHelperCallback
import ru.ssnexus.mymoviesearcher.view.MainActivity
import ru.ssnexus.mymoviesearcher.view.rv_adapters.FilmListRecyclerAdapter
import ru.ssnexus.mymoviesearcher.view.rv_adapters.TopSpacingItemDecoration
import ru.ssnexus.mymoviesearcher.viewmodel.FavoritesFragmentViewModel


class FavoritesFragment : Fragment() {
    init {
        exitTransition = Fade().apply { duration = 800;mode = Fade.MODE_OUT }
        reenterTransition = Fade().apply { duration = 800; }

    }

    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var filmsAdapter: FilmListRecyclerAdapter

    private val viewModel by lazy {
        ViewModelProvider.NewInstanceFactory().create(FavoritesFragmentViewModel::class.java)
    }
    private var filmsDataBase = listOf<Film>()
        //Используем backing field
        set(value) {
            //Если придет такое же значение, то мы выходим из метода
            if (field == value) return
            //Если пришло другое значение, то кладем его в переменную
            field = value
            //Обновляем RV адаптер
            filmsAdapter.addItems(field)
        }

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

        // Инициализируем RecyclerView
        rvInit()

        AnimationHelper.performFragmentCircularRevealAnimation(binding.root, requireActivity(), 2)
    }

    fun rvInit(){
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

        //Пордписываемся на данные модели
        viewModel.favFilmsListLiveData.observe(viewLifecycleOwner,
            {filmsAdapter.addItems(it)})

        viewModel.getData()

        val itemTouchHelper = ItemTouchHelper( object: ItemTouchHelperCallback(filmsAdapter)
        {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int)
            {
                super.onSwiped(viewHolder, direction)
                viewModel.removeFromFavorites(this.lastSwipedItem as Film)
            }

        })

        itemTouchHelper.attachToRecyclerView(binding.favoritesRecycler)
    }



}
