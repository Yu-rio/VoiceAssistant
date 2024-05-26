package com.example.voiceassistent.cityinfo


import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory


class HtmlWebService {
    fun getApi(): HtmlWebApi? {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://htmlweb.ru") //Базовая часть адреса
            .addConverterFactory(SimpleXmlConverterFactory.create())//Конвертер, необходимый для преобразования JSON'а в объекты
            .build()
        return retrofit.create(HtmlWebApi::class.java) //Создание объекта, при помощи которого будут выполняться запросы
    }

}