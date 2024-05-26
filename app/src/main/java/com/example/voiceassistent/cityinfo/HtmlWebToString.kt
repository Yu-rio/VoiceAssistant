package com.example.voiceassistent.cityinfo

import android.util.Log
import com.example.voiceassistent.AI
import com.example.voiceassistent.weather.Forecast
import com.example.voiceassistent.weather.ForecastApi
import com.example.voiceassistent.weather.ForecastService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.function.Consumer

class HtmlWebToString {
    fun getHtmlWeb(city: String?, callback: Consumer<MutableList<String>?>) {
        val api: HtmlWebApi? = HtmlWebService().getApi()
        val call: Call<HtmlWeb?>? = api?.getCityInfo(city)
        call!!.enqueue(object: Callback<HtmlWeb?> {
            override fun onResponse(call: Call<HtmlWeb?>, response: Response<HtmlWeb?>) {
                val result = response!!.body()
                if (result != null) {
                    val answer: MutableList<String> = ArrayList()
                    for(city in result.city?.msg!!) {
                        answer.add(
                            "Город ${city.name} точнее ${city.fullName} " +
                                    "расположен на ширине: ${city.latitude.toString()} " +
                                    "и долготе: ${city.longitude.toString()}\n" +
                                    "Взято с сайта: ${city.url}"
                        )
                    }
                    callback.accept(answer)
                } else {
                    callback.accept(null)
                }
            }

            override fun onFailure(call: Call<HtmlWeb?>, t: Throwable) {
                Log.w("CITY",t.toString())
                callback.accept(null)
            }
        })

    }
}