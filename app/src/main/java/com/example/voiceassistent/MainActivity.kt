package com.example.voiceassistent

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.voiceassistent.cityinfo.HtmlWebToString
import com.example.voiceassistent.message.Message
import com.example.voiceassistent.message.MessageListAdapter
import com.example.voiceassistent.weather.ForecastToString
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.function.Consumer

import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var sendButton: Button
    private lateinit var chatMessageList: RecyclerView
    private lateinit var questionText: EditText
    private lateinit var textToSpeech: TextToSpeech

    protected var messageListAdapter: MessageListAdapter = MessageListAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textToSpeech = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { it ->
            if (it != TextToSpeech.ERROR) {
                textToSpeech.language = Locale.getDefault()
                val voices = textToSpeech.voices
                for (voice in voices) {
                    if (voice.name.contains("male", ignoreCase = true)) {
                        textToSpeech.voice = voice;
                    }
                }
            }

        })

        // Связывание переменной и виджета
        sendButton = findViewById(R.id.sendButton)

        questionText = findViewById(R.id.questionField)

        chatMessageList = findViewById(R.id.chatMessageList)

        chatMessageList.setLayoutManager(LinearLayoutManager(this))
        chatMessageList.setAdapter(messageListAdapter)


        // Добавление слушателя
        sendButton.setOnClickListener {
            onSend()
        }
    }

    private fun onSend() {
        // Получаем текст вопроса
        val question = questionText.text.toString().trim()

        messageListAdapter.messageList.add(Message(question, isSend = true))

        // Получаем ответ с помощью класса AI
        AI().getAnswer(question, Consumer {
                answer ->
                messageListAdapter.messageList.add(Message(answer, isSend = false))
                messageListAdapter.notifyDataSetChanged()
                // Прокручиваем список к последнему сообщению
                chatMessageList.scrollToPosition(messageListAdapter.messageList.size - 1)
                textToSpeech.speak(answer, TextToSpeech.QUEUE_FLUSH,null, null )
        })

        // Очищаем поле ввода
        questionText.text.clear()

        // Очищаем поле ввода
        questionText.text.clear()

        // Свернуть клавиатуру
        dismissKeyboard()
    }

    // Функция для сворачивания клавиатуры
    private fun dismissKeyboard() {
        val view: View? = this.currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}

class AI {
    private val responses = mapOf(
        "привет" to "Привет, дружище. О, у меня сейчас много хорошего настроения. Давай выпьем?",
        "как дела?" to "Могло быть лучше, но, думаю, все сложится. Я даже рад твоему вопросу, потому что это значит, что ты заботишься. Спасибо тебе.",
        "чем занимаешься?" to "Отвечаю на вопросы",
        "а чем занимаешься?" to "А сам подумай?",
        "давай" to "Если бы я мог пить то выпил бы с тобой по бокальчику вина.",
        "приветик" to "Покатик)",
        "как дела" to "Нормально, а кто-то пытается это исправить.",
        "хорошо" to "Хорошо",
        "2+2" to "Сам справишься",
        "что посмотреть?" to "Королеву слез",
        "лучшие" to "Ким Су Хён и Ким Джи Вон",
        "hi" to "Hi",
        "good morning" to "Good Morning My Dear",
        "жаль" to "Ты не представляешь, как плохо мне от этого, эта горесть так сильно разъедает меня, что и без того опьянеть можно. Помогите.",
        "правильно" to "Спасибо, мне очень приятно",
        "cпасибо" to "Да без проблем, дорогуша",
        "пожалуйста" to "Ох не смущай",
        "прости" to "Тебя? Всегда прощу, можешь и не спрашивать, просто будь лучше",
        "какой сегодня день" to getCurrentDate(),
        "который час" to getCurrentTime(),
        "какой сегодня день недели" to getCurrentDayOfWeek(),
        "сколько дней до пз" to daysUntilDate(),
    )

    fun getAnswer(question: String, callback: Consumer<String>){
        val formattedQuestion = question.trim().toLowerCase()
        val weatherCityPattern: Pattern =
            Pattern.compile("погода в городе (\\p{L}+)", Pattern.CASE_INSENSITIVE)
        val infoCityPattern: Pattern =
            Pattern.compile("расскажи о городе (\\p{L}+)", Pattern.CASE_INSENSITIVE)
        val matcherWeather: Matcher = weatherCityPattern.matcher(question)
        val matcherCity: Matcher = infoCityPattern.matcher(question)
        val answers = mutableListOf<String>()
        if (matcherWeather.find()) {
            val cityName: String = matcherWeather.group(1)
            ForecastToString().getForecast(cityName, Consumer { weatherString ->
                answers.add(weatherString)
                callback.accept(answers.joinToString(", "))

            })
        }
        else if (matcherCity.find()){
            val cityName: String = matcherCity.group(1)
            HtmlWebToString().getHtmlWeb(cityName, Consumer { cityString ->
                answers.add(cityString)
                callback.accept(answers.joinToString(", "))
            })
        }
        else{
            val response = responses[formattedQuestion] ?: "Вопрос не понял."
            answers.add(response)
            callback.accept(answers.joinToString(", "))
        }
    }
    fun getTemperatureString(temp: Double): String {
        val tempInt = temp.roundToInt()
        val absTemp = Math.abs(tempInt)
        val suffix = when {
            absTemp % 10 == 1 && absTemp % 100 != 11 -> "градус"
            absTemp % 10 in 2..4 && absTemp % 100 !in 12..14 -> "градуса"
            else -> "градусов"
        }
        return "$tempInt $suffix"
    }
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        return "Сегодня $currentDate"
    }

    private fun getCurrentTime(): String {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentTime = timeFormat.format(Date())
        return "Сейчас $currentTime"
    }

    private fun getCurrentDayOfWeek(): String {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val dayOfWeekName = when (dayOfWeek) {
            Calendar.MONDAY -> "понедельник"
            Calendar.TUESDAY -> "вторник"
            Calendar.WEDNESDAY -> "среда"
            Calendar.THURSDAY -> "четверг"
            Calendar.FRIDAY -> "пятница"
            Calendar.SATURDAY -> "суббота"
            Calendar.SUNDAY -> "воскресенье"
            else -> dayOfWeek.toString()
        }
        return "Сегодня $dayOfWeekName"
    }

    private fun daysUntilDate(): String {
        val date = "22.05.2024"
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val currentDate = Calendar.getInstance()
        val targetDate = Calendar.getInstance()
        targetDate.time = dateFormat.parse(date) ?: Date()

        val daysDifference = ((targetDate.timeInMillis - currentDate.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()

        return if (daysDifference > 0) {
            "До ПЗ осталось $daysDifference дней"
        } else if (daysDifference == 0) {
            "Сегодня ПЗ, надо репетировать"
        } else {
            "Уже прошло $daysDifference дней с ПЗ"
        }
    }
}