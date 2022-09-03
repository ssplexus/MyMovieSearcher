package ru.ssnexus.database_module.data.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Observable
import ru.ssnexus.database_module.data.entity.Film

//Помечаем, что это не просто интерфейс, а Dao-объект
@Dao
interface FilmDao {
    //Запрос на всю таблицу
    @Query("SELECT * FROM cached_films")
    fun getCachedFilms(): Observable<List<Film>>

    @Query("SELECT * FROM cached_films")
    fun getCachedFilmsList(): List<Film>

    @Query("SELECT * FROM cached_films")
    fun getData(): List<Film>

    @Query("SELECT * FROM cached_films WHERE fav_state > 0")
    fun getFavorites(): List<Film>

    @Query("SELECT fav_state FROM cached_films WHERE id = :id")
    fun getFilmFavStateById(id: Int): Int

    @Query("SELECT * FROM cached_films WHERE watch_later_state > 0")
    fun getWatchLater(): List<Film>

    @Query("SELECT watch_later_state FROM cached_films WHERE id = :id")
    fun getFilmWatchLaterStateById(id: Int): Int

    //Кладём списком в БД, в случае конфликта перезаписываем
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<Film>)

    @Query("UPDATE cached_films SET fav_state = fav_state * (-1) WHERE id = :id")
    fun updateFavoriteById(id : Int);

    @Query("UPDATE cached_films SET watch_later_state = watch_later_state * (-1) WHERE id = :id")
    fun updateWatchLaterById(id : Int);

    // Очистка таблицы
    @Query("DELETE FROM cached_films")
    fun nukeTable()
}