package com.example.voiceassistent

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.voiceassistent.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var sendButton: Button
    private lateinit var chatWindow: TextView
    private lateinit var questionText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Связывание переменной и виджета
        sendButton = findViewById(R.id.sendButton)
        chatWindow = findViewById(R.id.chatWindow)
        questionText = findViewById(R.id.questionField)

        // Добавление слушателя
        sendButton.setOnClickListener {
            onSend()
        }
    }

    private fun onSend() {
        // Получаем текст вопроса
        val question = questionText.text.toString().trim()

        // Получаем ответ с помощью класса AI
        val answer = AI().getAnswer(question)

        // Отображаем вопрос и ответ в chatWindow
        chatWindow.append("  >> $question\n")
        chatWindow.append("  << $answer\n")

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
        "как дела??" to "Могло быть лучше, но, думаю, все сложится. Я даже рад твоему вопросу, потому что это значит, что ты заботишься. Спасибо тебе.",
        "чем занимаешься?" to "Отвечаю на вопросы",
        "a чем занимаешься?" to "А сам подумай?",
        "приветик" to "Покатик)",
        "как дела?" to "Нормально, а кто-то пытается это исправить.",
        "давай" to "Ох, если бы я мог пить, то пошел бы к столу с наполненной бутылкой вина. " +
                "Наполнил себе бокал и ждал, когда ты сделаешь то же самое. " +
                "Как только ты наполнил свой бокал, то поднял бы свой и указал на него, чтобы выпить вместе.",
        "Хорошо" to "Хорошо"
    )

    fun getAnswer(question: String): String {
        val formattedQuestion = question.trim().toLowerCase()
        return responses[formattedQuestion] ?: "Вопрос не понял."
    }
}