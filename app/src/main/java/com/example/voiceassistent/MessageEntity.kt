package com.example.voiceassistent

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.voiceassistent.message.Message
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date

class MessageEntity {
    var text: String = ""
    var isSend: Boolean = false
    var date: String = ""

    constructor(text: String, isSend: Boolean, date: String) {
        this.text = text
        this.date = date
        this.isSend = isSend
    }

    constructor(message: Message) {
        this.text = message.text
        var time = SimpleDateFormat("HH:mm")
        this.date = time.format(message.date)
        this.isSend = message.isSend
    }

}