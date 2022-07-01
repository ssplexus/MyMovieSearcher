package ru.ssnexus.mymoviesearcher.view.rv_adapters
import android.R.attr.data
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.film_item.view.*
import ru.ssnexus.mymoviesearcher.R
import ru.ssnexus.mymoviesearcher.data.entity.Film
import ru.ssnexus.mymoviesearcher.domain.ItemDiffUtil
import ru.ssnexus.mymoviesearcher.view.rv_viewholders.FilmViewHolder


//в параметр передаем слушатель, чтобы мы потом могли обрабатывать нажатия из класса Activity
class FilmListRecyclerAdapter(private val clickListener: OnItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    //Здесь у нас хранится список элементов для RV
    private var items = mutableListOf<Film>()

    fun setItems(items: List<Film>) {
        this.items = items as MutableList<Film>
    }

    fun getItems() : List<Film>{
        return items
    }

    fun clear()
    {
        items.clear()
    }

    //Этот метод нужно переопределить на возврат количества элементов в списке RV
    override fun getItemCount() = items.size

    //В этом методе мы привязываем наш ViewHolder и передаем туда "надутую" верстку нашего фильма
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context = LayoutInflater.from(parent.context)
        return FilmViewHolder(DataBindingUtil.inflate(context, R.layout.film_item, parent, false))
    }

    //В этом методе будет привязка полей из объекта Film к View из film_item.xml
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //Проверяем какой у нас ViewHolder
        when (holder) {
            is FilmViewHolder -> {
                 var filmViewHolder : FilmViewHolder = holder

                //Вызываем метод bind(), который мы создали, и передаем туда объект
                //из нашей базы данных с указанием позиции
                filmViewHolder.bind(items[position])
                //Обрабатываем нажатие на весь элемент целиком(можно сделать на отдельный элемент
                //например, картинку) и вызываем метод нашего листенера, который мы получаем из
                //конструктора адаптера
                filmViewHolder.binding.root.item_container.setOnClickListener {
                    clickListener.click(items[position])
                }
                // Анимирование рейтинга
                filmViewHolder.binding.root.rating_donut.animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.scale_animation)
            }
        }
    }


    //Метод для добавления объектов в наш список
    fun addItems(list: List<Film>) {

        val newList = arrayListOf<Film>()
        //newList.addAll(getItems() + list)
        newList.addAll(list)
        setItems(newList)
        notifyDataSetChanged()
//        val  diff = ItemDiffUtil(getItems(), newList)
//        val difResult = DiffUtil.calculateDiff(diff)
//        setItems(newList)
//        difResult.dispatchUpdatesTo(this)
    }

    fun clearRV(){
        val size: Int = items.size
        items.clear()
        notifyItemRangeRemoved(0, size)
    }

    //Интерфейс для обработки кликов
    interface OnItemClickListener {
        fun click(film: Film)
    }
}