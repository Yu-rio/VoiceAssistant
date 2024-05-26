package com.example.voiceassistent.weather

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "current", strict = false)
class Forecast {
    @field:Element(name="temperature")
    var temperature: Temperature? = null
    @field:Element(name="weather")
    var weather: Weather? = null
    @Root(name = "temperature", strict = false)
    class Temperature {
        @field:Attribute(name = "value")
        var value: String? = null
    }

    @Root(name = "weather", strict = false)
    class Weather  {
        @field:Attribute(name = "value")
        var value: String? = null
    }


}
