package com.example.voiceassistent.message

import java.util.Date

data class Message(val text: String,
                   val isSend: Boolean,
                   val date: Date = Date()) {

}
