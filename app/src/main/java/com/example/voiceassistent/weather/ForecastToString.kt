package com.example.voiceassistent.weather


import android.util.Log
import com.example.voiceassistent.AI
import com.example.voiceassistent.weather.Forecast
import com.example.voiceassistent.weather.ForecastApi
import com.example.voiceassistent.weather.ForecastService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.function.Consumer


class ForecastToString {
    fun getForecast(city: String?, callback: Consumer<String>) {
        val api: ForecastApi? = ForecastService().getApi()
        val call: Call<Forecast?>? = api?.getCurrentWeather(city)
        call!!.enqueue(object: Callback<Forecast?>{
            override fun onResponse(call: Call<Forecast?>, response: Response<Forecast?>) {
                val result = response!!.body()

                if (result != null){
                    val ai = AI()
                    val temperature = result.temperature?.value?.toDouble() ?: 0.0
                    val temperatureString = ai.getTemperatureString(temperature)
                    val answer = "Сейчас где-то $temperatureString и ${result.weather?.value}"
                    callback.accept(answer)
                }
                else callback.accept("Не могу узнать погоду")
            }

            override fun onFailure(call: Call<Forecast?>, t: Throwable) {
                Log.w("WEATHER",t.toString())
                callback.accept("Не могу узнать погоду")
            }
        })

    }
}