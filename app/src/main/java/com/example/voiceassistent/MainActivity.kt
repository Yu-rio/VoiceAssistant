package com.example.voiceassistent

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.voiceassistent.message.Message
import com.example.voiceassistent.message.MessageListAdapter
import java.util.Locale
import java.util.function.Consumer

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

