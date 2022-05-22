package ru.ssnexus.mymoviesearcher.view.rv_viewholders

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.ssnexus.mymoviesearcher.R
import ru.ssnexus.mymoviesearcher.data.ApiConstants
import ru.ssnexus.mymoviesearcher.databinding.FilmItemBinding
import ru.ssnexus.mymoviesearcher.domain.Film

class FilmViewHolder(val binding: FilmItemBinding) : RecyclerView.ViewHolder(binding.root) {
    //Привязываем View из layout к переменным
    private val title = binding.title
    private val poster = binding.poster
    private val description = binding.description

    //В этом методе кладем данные из Film в наши View
    fun bind(film: Film) {
        //Устанавливаем заголовок
        title.text = film.title

        //Указываем контейнер, в котором будет "жить" наша картинка
        Glide.with(itemView)
            //Загружаем сам ресурс
            .load(ApiConstants.IMAGES_URL + binding.root.resources.getString(R.string.poster_small) + film.poster)
            //Центруем изображение
            .centerCrop()
            //Указываем ImageView, куда будем загружать изображение
            .into(poster)

        //Устанавливаем описание
        description.text = film.description
        //Устанавливаем рэйтинг
        binding.rating = (film.rating * 10).toInt()
    }
}