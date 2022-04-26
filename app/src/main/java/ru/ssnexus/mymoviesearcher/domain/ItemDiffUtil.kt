package ru.ssnexus.mymoviesearcher.domain

import androidx.recyclerview.widget.DiffUtil
import ru.ssnexus.mymoviesearcher.domain.Film
import ru.ssnexus.mymoviesearcher.domain.Item

class ItemDiffUtil(val oldList: List<Item>, val newList: List<Item>): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldCard = oldList[oldItemPosition] as Film
        val newCard = newList[newItemPosition] as Film
        return oldCard.title == newCard.title && oldCard.description == newCard.description &&
                oldCard.poster == newCard.poster
    }
}