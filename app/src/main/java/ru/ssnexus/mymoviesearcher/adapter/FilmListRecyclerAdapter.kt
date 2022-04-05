package ru.ssnexus.mymoviesearcher.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.film_item.*
import kotlinx.android.synthetic.main.film_item.view.*
import ru.ssnexus.mymoviesearcher.R
import ru.ssnexus.mymoviesearcher.model.Film
import ru.ssnexus.mymoviesearcher.model.Item
import ru.ssnexus.mymoviesearcher.model.ItemDiffUtil

//в параметр передаем слушатель, чтобы мы потом могли обрабатывать нажатия из класса Activity
class FilmListRecyclerAdapter(private val clickListener: OnItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    //Здесь у нас хранится список элементов для RV
    private var items = mutableListOf<Film>()

    fun setItems(items: List<Item>) {
        this.items = items as MutableList<Film>
    }

    fun getItems() : List<Film>{
        return items
    }

    //Этот метод нужно переопределить на возврат количества элементов в списке RV
    override fun getItemCount() = items.size

    //В этом методе мы привязываем наш ViewHolder и передаем туда "надутую" верстку нашего фильма
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return FilmViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.film_item, parent, false))
    }

    //В этом методе будет привязка полей из объекта Film к View из film_item.xml
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //Проверяем какой у нас ViewHolder
        when (holder) {
            is FilmViewHolder -> {
                //Вызываем метод bind(), который мы создали, и передаем туда объект
                //из нашей базы данных с указанием позиции
                holder.bind(items[position])
                //Обрабатываем нажатие на весь элемент целиком(можно сделать на отдельный элемент
                //например, картинку) и вызываем метод нашего листенера, который мы получаем из
                //конструктора адаптера
                holder.itemView.item_container.setOnClickListener {
                    clickListener.click(items[position])
                }
                // Анимирование рейтинга
                holder.itemView.rating_donut.animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.scale_animation)
            }
        }
    }


    //Метод для добавления объектов в наш список
    fun addItems(list: List<Item>) {

        val newList = arrayListOf<Item>()
        newList.addAll(list)
        val  diff = ItemDiffUtil(getItems(), newList)
        val difResult = DiffUtil.calculateDiff(diff)
        setItems(newList)
        difResult.dispatchUpdatesTo(this)
    }

    //Интерфейс для обработки кликов
    interface OnItemClickListener {
        fun click(film: Film)
    }
}