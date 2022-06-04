package ru.ssnexus.mymoviesearcher.view.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.isEmpty
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.android.ext.android.bind
import ru.ssnexus.mymoviesearcher.App
import ru.ssnexus.mymoviesearcher.data.MainRepository
import ru.ssnexus.mymoviesearcher.databinding.FragmentHomeBinding
import ru.ssnexus.mymoviesearcher.domain.Film
import ru.ssnexus.mymoviesearcher.domain.Item
import ru.ssnexus.mymoviesearcher.utils.AnimationHelper
import ru.ssnexus.mymoviesearcher.view.MainActivity
import ru.ssnexus.mymoviesearcher.view.rv_adapters.FilmListRecyclerAdapter
import ru.ssnexus.mymoviesearcher.view.rv_adapters.TopSpacingItemDecoration
import ru.ssnexus.mymoviesearcher.viewmodel.HomeFragmentViewModel
import timber.log.Timber
import java.util.*
import kotlin.math.roundToInt


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var filmsAdapter: FilmListRecyclerAdapter

    private val viewModel by lazy {
        ViewModelProvider.NewInstanceFactory().create(HomeFragmentViewModel::class.java)
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

    private var direction : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AnimationHelper.performFragmentCircularRevealAnimation(home_fragment_root, requireActivity(), 1)

        initSearchView()
        initPullToRefresh()
        // Инициализируем RecyclerView
        initRecycler()
    }


    private fun initPullToRefresh() {

        //Делаем refresh на swipe up
            binding.mainRecycler.setOnTouchListener(object: View.OnTouchListener {
                override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                    if (p1?.action == android.view.MotionEvent.ACTION_UP) {
                        Timber.d("Swipe up!")
                        if(binding.mainRecycler.isEmpty()) return false
                        val lManager = binding.mainRecycler.layoutManager
                        if (lManager is LinearLayoutManager)
                        {
                            if(lManager.findLastCompletelyVisibleItemPosition() == viewModel.totalPageResults - 1)
                            {
                                binding.pullToRefresh.isRefreshing = true

                                //Делаем новый запрос фильмов на сервер
                                viewModel.getFilms(1)
                                //Убираем крутящиеся колечко
                               binding.pullToRefresh.isRefreshing = false
                            }

                        }
                    }
                    return false
                }
            })

            //Вешаем слушатель, чтобы вызвался pull to refresh
            binding.pullToRefresh.setOnRefreshListener {
                // filmsAdapter.clear()
                //Делаем новый запрос фильмов на сервер
                viewModel.getFilms(-1)
                //Убираем крутящиеся колечко
                binding.pullToRefresh.isRefreshing = false
            }
        }


    private fun initSearchView() {
        binding.searchView.setOnClickListener {
            binding.searchView.isIconified = false
        }

        //Подключаем слушателя изменений введенного текста в поиска
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            //Этот метод отрабатывает при нажатии кнопки "поиск" на софт клавиатуре
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
            //Этот метод отрабатывает на каждое изменения текста
            override fun onQueryTextChange(newText: String?): Boolean {
                //Если ввод пуст то вставляем в адаптер всю БД
                if(newText == null) return false
                if (newText.isEmpty()) {
                    filmsAdapter.addItems(filmsDataBase)
                    return true
                }
                //Фильтруем список на поискк подходящих сочетаний
                val result = filmsDataBase.filter {
                    //Чтобы все работало правильно, нужно и запрос, и имя фильма приводить к нижнему регистру
                    @OptIn(kotlin.ExperimentalStdlibApi::class)
                    it.title.lowercase(Locale.getDefault()).contains(newText.lowercase(Locale.getDefault()))
                }
                //Добавляем в адаптер
                filmsAdapter.addItems(result)
                return true
            }
        })
    }

    private fun initRecycler(){

        //находим наш RV
        binding.mainRecycler.apply {

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

            viewModel.filmsListLiveData.observe(viewLifecycleOwner,
                Observer<List<Film>>{
                    filmsDataBase = it;
                    // Скролл к последнему элементу предыдущей страницы
                    Timber.d("Scroll to position " + viewModel.scrollToPosition)
                    if (viewModel.scrollToPosition > 0)
                    {
                        scrollToPosition(viewModel.scrollToPosition)
                        viewModel.scrollToPosition = 0
                    }
                })
        }
    }
}