package ru.ssnexus.mymoviesearcher.utils

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ru.ssnexus.mymoviesearcher.domain.Film
import ru.ssnexus.mymoviesearcher.view.rv_adapters.FilmListRecyclerAdapter
import ru.ssnexus.mymoviesearcher.domain.Item
import ru.ssnexus.mymoviesearcher.domain.ItemDiffUtil

open class ItemTouchHelperCallback(val adapter : FilmListRecyclerAdapter): ItemTouchHelper.Callback() {

    lateinit var lastSwipedItem: Item

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val swipeFlags = ItemTouchHelper.END
        return makeMovementFlags(0, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val newList = arrayListOf<Item>()
        newList.addAll(adapter.getItems())
        lastSwipedItem = newList.removeAt(viewHolder.adapterPosition)
        val  diff = ItemDiffUtil(adapter.getItems(), newList)
        val difResult = DiffUtil.calculateDiff(diff)
        adapter.setItems(newList)
        difResult.dispatchUpdatesTo(adapter)
    }
}