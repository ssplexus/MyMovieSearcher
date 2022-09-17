package ru.ssnexus.mymoviesearcher.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.*
import ru.ssnexus.database_module.data.entity.Film
import ru.ssnexus.mymoviesearcher.utils.AnimationHelper
import ru.ssnexus.mymoviesearcher.databinding.FragmentWatchLaterBinding
import ru.ssnexus.mymoviesearcher.view.MainActivity
import ru.ssnexus.mymoviesearcher.view.rv_adapters.FilmListRecyclerAdapter
import ru.ssnexus.mymoviesearcher.view.rv_adapters.TopSpacingItemDecoration
import ru.ssnexus.mymoviesearcher.viewmodel.WatchLaterFragmentViewModel

class WatchLaterFragment : Fragment() {

    private lateinit var binding: FragmentWatchLaterBinding
    private lateinit var filmsAdapter: FilmListRecyclerAdapter
    private val scope = CoroutineScope(Dispatchers.IO)

    private val viewModel by lazy {
        ViewModelProvider.NewInstanceFactory().create(WatchLaterFragmentViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWatchLaterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Инициализируем RecyclerView
        rvInit()
        AnimationHelper.performFragmentCircularRevealAnimation(
            binding.watchlaterFragmentRoot,
            requireActivity(),
            3
        )
    }

    fun rvInit(){
        //находим наш RV
        binding.watchLaterRecycler.apply {

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
        viewModel.watchLaterFilmsListLiveData.observe(viewLifecycleOwner,
            {filmsAdapter.addItems(it)})

        MainScope().launch {
            scope.async {
                viewModel.getData()
            }
        }
    }
}