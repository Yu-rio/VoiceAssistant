package com.example.voiceassistent.weather

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

class ForecastService {
    fun getApi(): ForecastApi? {
        val retrofit = Retrofit.Builder()
            //.baseUrl("https://openweathermap.org/current  " + что?
            .baseUrl("https://api.openweathermap.org") //Базовая часть адреса
            .addConverterFactory(SimpleXmlConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
            .build()
        return retrofit.create(ForecastApi::class.java) //Создание объекта, при помощи которого будут выполняться запросы
    }

}