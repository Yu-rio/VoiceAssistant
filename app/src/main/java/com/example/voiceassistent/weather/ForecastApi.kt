package com.example.voiceassistent.weather


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ForecastApi {
    @GET("/data/2.5/weather?limit=1&units=metric&mode=xml&appid=847454627d0da0b043db7b097168e2cb&lang=ru")
    fun getCurrentWeather(@Query("q") city: String?): Call<Forecast?>?

}