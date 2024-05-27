package com.example.voiceassistent


import android.content.ContentValues
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.voiceassistent.message.Message
import com.example.voiceassistent.message.MessageListAdapter

import java.io.Serializable
import java.util.Date
import java.util.Locale
import java.util.function.Consumer

class MainActivity : AppCompatActivity() {

    private lateinit var sendButton: Button
    private lateinit var chatMessageList: RecyclerView
    private lateinit var questionText: EditText
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var toolBar: Toolbar
    val APP_PREFERENCES = "mysettings"
    var sPref: SharedPreferences? = null
    private var isLight = true
    private val THEME = "THEME"
    var dBHelper: DBHelper? = null
    var dataBase: SQLiteDatabase? = null



    protected var messageListAdapter: MessageListAdapter = MessageListAdapter()

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.clear_dialog->{
                dataBase?.delete(dBHelper?.TABLE_NAME, null, null)
                messageListAdapter.messageList.clear()
                messageListAdapter.notifyDataSetChanged()
            }
            R.id.day_setting -> {
                delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                isLight = true
            }
            R.id.night_setting -> {
                delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                isLight = false
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun fillMessages() {
        val cursor: Cursor = dataBase!!.query(
            dBHelper!!.TABLE_NAME,
            null, null, null,
            null, null, null
        )
        if (cursor.moveToFirst()) {
            val messageIndex = cursor.getColumnIndex(dBHelper!!.FIELD_MESSAGE)
            val dateIndex = cursor.getColumnIndex(dBHelper!!.FIELD_DATE)
            val sendIndex = cursor.getColumnIndex(dBHelper!!.FIELD_SEND)
            do {
                val entity = MessageEntity(
                    cursor.getString(messageIndex),
                    cursor.getInt(sendIndex) == 1,
                    cursor.getString(dateIndex)
                )
                val message = Message("", false, Date()).getMessage(entity)
                messageListAdapter.messageList.add(message)
            } while (cursor.moveToNext())
        }
        cursor.close()
        chatMessageList.scrollToPosition(messageListAdapter.messageList.size - 1)
    }
    override fun onStop() {
        val editor: SharedPreferences.Editor = sPref!!.edit()
        editor.putBoolean(THEME, isLight)
        dataBase?.delete(dBHelper?.TABLE_NAME, null, null)
        editor.apply()
        saveMessages()
        super.onStop()
    }
    private fun saveMessages() {
        for (i in 0..<messageListAdapter.messageList.size) {
            val entity = MessageEntity(messageListAdapter.messageList[i])
            val contentValues = ContentValues()
            contentValues.put(dBHelper?.FIELD_MESSAGE, entity.text)
            contentValues.put(dBHelper?.FIELD_SEND, entity.isSend)
            contentValues.put(dBHelper?.FIELD_DATE, entity.date)
            dataBase?.insert(dBHelper?.TABLE_NAME, null, contentValues)
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        outState.run {
            putSerializable("messageList", messageListAdapter.messageList as Serializable)
        }
        super.onSaveInstanceState(outState)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState != null) {
            messageListAdapter.messageList = savedInstanceState.getSerializable("messageList") as ArrayList<Message>
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        sPref = getSharedPreferences(APP_PREFERENCES,MODE_PRIVATE)
        isLight = sPref!!.getBoolean(THEME, true)
        if (!isLight) {
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
        }
        else {delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO}
        dBHelper = DBHelper(this)
        dataBase = dBHelper!!.writableDatabase


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i("LOG", "onCreate")
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
        toolBar = findViewById(R.id.toolbar)
        setSupportActionBar(toolBar)
        getSupportActionBar()!!.setDisplayShowTitleEnabled(false)



        // Связывание переменной и виджета
        sendButton = findViewById(R.id.sendButton)

        questionText = findViewById(R.id.questionField)

        chatMessageList = findViewById(R.id.chatMessageList)

        chatMessageList.setLayoutManager(LinearLayoutManager(this))
        chatMessageList.setAdapter(messageListAdapter)

        if (savedInstanceState != null) {
            messageListAdapter.messageList =
                savedInstanceState.getSerializable("messageList") as MutableList<Message>
        } else {
            fillMessages()
        }

        // Добавление слушателя
        sendButton.setOnClickListener {
            onSend()
        }
    }

    override fun onDestroy() {
        dataBase!!.close()
        super.onDestroy()
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

