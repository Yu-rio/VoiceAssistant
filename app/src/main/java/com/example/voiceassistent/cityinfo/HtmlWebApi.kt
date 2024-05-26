package com.example.voiceassistent.cityinfo


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface HtmlWebApi {
    @GET("/xml/geo/search?api_key=7fc16e8f4bb00cabe915e6254dee7e31")
    fun getCityInfo(@Query("search") city: String?): Call<HtmlWeb?>?
}