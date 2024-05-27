package com.example.voiceassistent.message

import com.example.voiceassistent.MessageEntity
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

data class Message(val text: String,
                   val isSend: Boolean,
                   val date: Date = Date()) {
    fun getMessage(messageEntity: MessageEntity) : Message {
        var time = SimpleDateFormat("HH:mm")
        return Message(messageEntity.text, messageEntity.isSend, time.parse(messageEntity.date))
    }
}
