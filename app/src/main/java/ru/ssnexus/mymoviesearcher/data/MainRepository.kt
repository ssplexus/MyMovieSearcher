package ru.ssnexus.mymoviesearcher.data

import android.content.ContentValues
import android.database.Cursor
import ru.ssnexus.mymoviesearcher.R
import ru.ssnexus.mymoviesearcher.data.db.DatabaseHelper
import ru.ssnexus.mymoviesearcher.domain.Film

class MainRepository(databaseHelper: DatabaseHelper) {
    val favoritesFilms = mutableListOf<Film>()

    //Инициализируем объект для взаимодействия с БД
    private val sqlDb = databaseHelper.readableDatabase
    //Создаем курсор для обработки запросов из БД
    private lateinit var cursor: Cursor

    fun putToDb(film: Film) {
        //Создаем объект, который будет хранить пары ключ-значение, для того
        //чтобы класть нужные данные в нужные столбцы
        val cv = ContentValues()
        cv.apply {
            put(DatabaseHelper.COLUMN_FILM_ID, film.id)
            put(DatabaseHelper.COLUMN_TITLE, film.title)
            put(DatabaseHelper.COLUMN_POSTER, film.poster)
            put(DatabaseHelper.COLUMN_DESCRIPTION, film.description)
            put(DatabaseHelper.COLUMN_RATING, film.rating)
        }
        //Кладем фильм в БД
        sqlDb.insert(DatabaseHelper.TABLE_NAME, null, cv)
    }

    fun getAllFromDB(): List<Film> {
        //Создаем курсор на основании запроса "Получить все из таблицы"
        cursor = sqlDb.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_NAME}", null)
        //Сюда будем сохранять результат получения данных
        val result = mutableListOf<Film>()
        //Проверяем, есть ли хоть одна строка в ответе на запрос
        if (cursor.moveToFirst()) {
            //Итерируемся по таблице, пока есть записи, и создаем на основании объект Film
            do {
                val film_id = cursor.getInt(1)
                val title = cursor.getString(2)
                val poster = cursor.getString(3)
                val description = cursor.getString(4)
                val rating = cursor.getDouble(5)

                result.add(Film(film_id, title, poster, description, rating))
            } while (cursor.moveToNext())
        }
        //Возвращаем список фильмов
        return result
    }
}
