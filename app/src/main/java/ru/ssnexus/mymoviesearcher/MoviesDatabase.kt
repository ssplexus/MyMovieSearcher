package ru.ssnexus.mymoviesearcher

import ru.ssnexus.mymoviesearcher.model.Film

class MoviesDatabase () {

    lateinit var db: List<Film>

    fun setDB(newDb:List<Film>) {
        db = newDb
    }
    fun getDB(): List<Film> {
            return db
    }
    fun getFavorites() : List<Film>
    {
        val favList = ArrayList<Film>(db)
        return favList?.filter { it.isInFavorites }
    }

    fun addItem(film: Film)
    {
        (db as MutableList<Film>).add(film)
    }

}