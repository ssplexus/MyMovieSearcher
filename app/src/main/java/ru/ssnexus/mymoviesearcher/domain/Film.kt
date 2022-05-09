package ru.ssnexus.mymoviesearcher.domain

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import kotlinx.android.parcel.Parcelize
import ru.ssnexus.mymoviesearcher.BR

@Parcelize
data class Film(override val id: Int,
    val title: String,
    val poster: String, //У нас будет приходить ссылка на картинку, так что теперь это String
    val description: String,
    var rating: Double = 0.0, //Приходит не целое число с API
    var isInFavorites: Boolean = false
) : Parcelable, Item

/*
@Parcelize
data class Film(override val id: Int,
                val title:String,
                @DrawableRes val poster:Int,
                val description:String,
                private var _rating: Float = 0f,
                var isInFavorites: Boolean = false): BaseObservable(), Parcelable,
    Item
{
    @get:Bindable
    var rating: Float = _rating
        set(value) {
            field = value
            notifyPropertyChanged(BR.rating)
        }
}
*/