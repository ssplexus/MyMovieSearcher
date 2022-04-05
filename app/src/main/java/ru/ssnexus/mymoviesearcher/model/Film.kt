package ru.ssnexus.mymoviesearcher.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.android.parcel.Parcelize
import ru.ssnexus.mymoviesearcher.model.Item

@Parcelize
data class Film(override val id: Int,
                val title:String,
                @DrawableRes val poster:Int,
                val description:String,
                var rating: Float = 0f,
                var isInFavorites: Boolean = false):Parcelable,
    Item
