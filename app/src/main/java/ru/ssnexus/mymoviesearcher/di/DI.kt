package ru.ssnexus.mymoviesearcher.di

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.ssnexus.mymoviesearcher.BuildConfig
import ru.ssnexus.mymoviesearcher.data.ApiConstants
import ru.ssnexus.mymoviesearcher.data.MainRepository
import ru.ssnexus.mymoviesearcher.data.TmdbApi
import ru.ssnexus.mymoviesearcher.domain.Interactor
import java.util.concurrent.TimeUnit

object DI {

    val mainModule = module {
        //Создаем репозиторий
        single { MainRepository() }

        //Создаем объект для получения данных из сети
        single<TmdbApi> {
            //Создаём кастомный клиент
            val okHttpClient = OkHttpClient.Builder()
                //Настраиваем таймауты для медленного интернета
                .callTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                //Добавляем логгер
                .addInterceptor(HttpLoggingInterceptor().apply {
                    if (BuildConfig.DEBUG) {
                        level = HttpLoggingInterceptor.Level.BASIC
                    }
                })
                .build()


            //Создаем Ретрофит
            val retrofit = Retrofit.Builder()
                //Указываем базовый URL из констант
                .baseUrl(ApiConstants.BASE_URL)
                //Добавляем конвертер
                .addConverterFactory(GsonConverterFactory.create())
                //Добавляем кастомный клиент
                .client(okHttpClient)
                .build()
            //Создаем сам сервис с методами для запросов
            retrofit.create(TmdbApi::class.java)
        }
        //Создаем интректор
        single { Interactor(get(), get()) }
    }
}


//Создаем объект для получения данных из сети
//single<TmdbApi> {
//    val okHttpClient = OkHttpClient.Builder()
//        //Настраиваем таймауты для медленного интернета
//        .callTimeout(30, TimeUnit.SECONDS)
//        .readTimeout(30, TimeUnit.SECONDS)
//        //Добавляем логгер
//        .addInterceptor(HttpLoggingInterceptor().apply {
//            if (BuildConfig.DEBUG) {
//                level = HttpLoggingInterceptor.Level.BASIC
//            }
//        })
//        .build()
//    //Создаем ретрофит
//    val retrofit = Retrofit.Builder()
//        //Указываем базовый URL из констант
//        .baseUrl(ApiConstants.BASE_URL)
//        //Добавляем конвертер
//        .addConverterFactory(GsonConverterFactory.create())
//        //Добавляем кастомный клиент
//        .client(okHttpClient)
//        .build()
//    //Создаем сам сервис с методами для запросов
//    retrofit.create(TmdbApi::class.java)
//}
////Создаем интерактор
//single { Interactor(get(), get()) }
