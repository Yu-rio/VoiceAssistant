package com.example.voiceassistent.cityinfo

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root


@Root(name = "message", strict = false)
class HtmlWeb {
    @field:Element(name = "city")
    var city: City? = null
}

@Root(name = "city", strict = false)
class City {
    @field:ElementList(inline = true, name = "msg")
    var msg: List<Msg>? = null
}

@Root(name = "msg", strict = false)
class Msg {
    @field:Element(name = "name")
    var name: String? = null

    @field:Element(name = "latitude")
    var latitude: Double? = null

    @field:Element(name = "longitude")
    var longitude: Double? = null

    @field:Element(name = "full_name")
    var fullName: String? = null

    @field:Element(name = "url")
    var url: String? = null
}