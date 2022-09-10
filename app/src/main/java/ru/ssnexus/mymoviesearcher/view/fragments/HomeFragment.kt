package ru.ssnexus.mymoviesearcher.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isEmpty
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.ssnexus.database_module.data.entity.Film
import ru.ssnexus.mymoviesearcher.databinding.FragmentHomeBinding
import ru.ssnexus.mymoviesearcher.utils.AnimationHelper
import ru.ssnexus.mymoviesearcher.utils.AutoDisposable
import ru.ssnexus.mymoviesearcher.utils.addTo
import ru.ssnexus.mymoviesearcher.view.MainActivity
import ru.ssnexus.mymoviesearcher.view.rv_adapters.FilmListRecyclerAdapter
import ru.ssnexus.mymoviesearcher.view.rv_adapters.TopSpacingItemDecoration
import ru.ssnexus.mymoviesearcher.viewmodel.HomeFragmentViewModel
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var filmsAdapter: FilmListRecyclerAdapter

    private val autoDisposable = AutoDisposable()

    private val viewModel by lazy {
        ViewModelProvider.NewInstanceFactory().create(HomeFragmentViewModel::class.java)
    }
    private var filmsDataBase = listOf<Film>()
        //Используем backing field
        set(value) {
            //Если пришло другое значение, то кладем его в переменную
            field = value
            //Обновляем RV адаптер
            filmsAdapter.addItems(field)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        autoDisposable.bindTo(lifecycle)
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
        AnimationHelper.performFragmentCircularRevealAnimation(binding.homeFragmentRoot, requireActivity(), 1)

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
                        if(binding.mainRecycler.isEmpty()) return false
                        val lManager = binding.mainRecycler.layoutManager

                        if (lManager is LinearLayoutManager)
                        {
                            Timber.d("Swipe up!" + lManager.findLastCompletelyVisibleItemPosition() + " " + lManager.itemCount )
                            if(lManager.findLastCompletelyVisibleItemPosition() >= lManager.itemCount - 10)
                            {
                                binding.pullToRefresh.isRefreshing = true

                                //Делаем новый запрос фильмов на сервер если не режим поиска
                                if(!viewModel.getSearch()) viewModel.getFilms()
                                //Иначе получаем следующую страницу результата поиска
                                else {
                                    viewModel.getSearchResult(binding.searchView.query.toString()).runSearch()
                                }

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

                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.interactor.clearCache()
                }

                when(!viewModel.getSearch()){
                    //Делаем новый запрос фильмов на сервер
                    true -> viewModel.updateFilms()
                    //Показываем первую страницу поиска
                    false -> {
                        viewModel.currentSearchPage = 0
                        viewModel.getSearchResult(binding.searchView.query.toString()).runSearch()
                    }
                }

                //Убираем крутящиеся колечко
                binding.pullToRefresh.isRefreshing = false
            }
        }

    private fun initSearchView() {
        binding.searchView.setOnClickListener {
            binding.searchView.isIconified = false
        }

        Observable.create(ObservableOnSubscribe<String>{subscriber ->
            //Вешаем слушатель на клавиатуру
            binding.searchView.setOnQueryTextListener(object :
            //Вызывается на ввод символов
                SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String): Boolean {
                    Timber.d("onQueryTextChange")
                    viewModel.setSearch()
                    subscriber.onNext(newText)
                    return false
                }

                //Вызывается по нажатию кнопки "Поиск"
                override fun onQueryTextSubmit(query: String): Boolean {
                    Timber.d("onQueryTextSubmit")
                    viewModel.setSearch()
                    subscriber.onNext(query)
                    return false
                }
            })
        })
        .subscribeOn(Schedulers.io())
        .map {
            it.toLowerCase(Locale.getDefault()).trim()
        }
        .debounce(800, TimeUnit.MILLISECONDS)
        .filter {

            //Если в поиске пустое поле, возвращаем список фильмов по умолчанию
            if(!it.isNotBlank()) {
                Timber.d("Blank")
                viewModel.setSearch(false)
            }
            it.isNotBlank()
        }
        .flatMap {
            Timber.d("Search = " + it)
            viewModel.getSearchResult(it)
        }
            .runSearch()


        binding.searchView.setOnClickListener {
            binding.searchView.isIconified = false
        }
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

            viewModel.filmsListData
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { list ->
                    Timber.d("Data!!!")
                    filmsDataBase = list
                }.addTo(autoDisposable)

            viewModel.showProgressBar
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe{
                    binding.progressBar.isVisible = it
                }.addTo(autoDisposable)
        }
    }

    // Запуск поиска
    fun Observable<List<Film>>.runSearch(){
        this.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = {
                    Timber.d("Что-то пошло не так")
                    //Toast.makeText(requireContext(), "Что-то пошло не так", Toast.LENGTH_SHORT).show()
                },
                onNext = {
                    Timber.d("OnNext " + it.size)
                    filmsAdapter.addItems(it)
                    binding.mainRecycler.scrollToPosition(0)
                }
            )
            .addTo(autoDisposable)
    }

}