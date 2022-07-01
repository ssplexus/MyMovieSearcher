package ru.ssnexus.mymoviesearcher.view.fragments

import android.content.Intent
import android.os.Bundle
import android.transition.Fade
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import ru.ssnexus.mymoviesearcher.R
import ru.ssnexus.mymoviesearcher.data.ApiConstants
import ru.ssnexus.mymoviesearcher.databinding.FragmentDetailsBinding
import ru.ssnexus.mymoviesearcher.data.entity.Film
import ru.ssnexus.mymoviesearcher.viewmodel.DetailsFragmentViewModel

class DetailsFragment : Fragment() {
    private lateinit var binding:FragmentDetailsBinding

    private val viewModel by lazy {
        ViewModelProvider.NewInstanceFactory().create(DetailsFragmentViewModel::class.java)
    }

    init {
        enterTransition = Fade().apply { duration = 800 }
        returnTransition = Fade().apply { duration = 100;mode = Fade.MODE_OUT }
        exitTransition = Fade().apply { duration = 800 }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Получаем наш фильм из переданного бандла
        val film = arguments?.get(R.string.parcel_item_film.toString()) as Film


        viewModel.checkInFavorites(film)

        //Устанавливаем заголовок
        binding.detailsToolbar.title = film.title
        //Устанавливаем картинку
        Glide.with(this)
            .load(ApiConstants.IMAGES_URL + resources.getString(R.string.poster_big) + film.poster)
            .centerCrop()
            .into(binding.detailsPoster)

        //Устанавливаем описание
        binding.detailsDescription.text = film.description

        binding.detailsFabFav.setImageResource(
            if (film.isInFavorites) R.drawable.ic_baseline_favorite_24
            else R.drawable.ic_baseline_favorite_border_24
        )

        binding.detailsFabFav.setOnClickListener {
            if (!film.isInFavorites) {
                binding.detailsFabFav.setImageResource(R.drawable.ic_baseline_favorite_24)
                film.isInFavorites = true
                viewModel.addToFavorites(film)
            } else {
                binding.detailsFabFav.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                film.isInFavorites = false
                viewModel.removeFromFavorites(film)
            }
        }

        binding.detailsFabShare.setOnClickListener {
            //Создаем интент
            val intent = Intent()
            //Указываем action с которым он запускается
            intent.action = Intent.ACTION_SEND
            //Кладем данные о нашем фильме
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "Check out this film: ${film.title} \n\n ${film.description}"
            )
            //Указываем MIME тип, чтобы система знала, какое приложения предложить
            intent.type = "text/plain"
            //Запускаем наше активити
            startActivity(Intent.createChooser(intent, "Share To:"))
        }
    }
}