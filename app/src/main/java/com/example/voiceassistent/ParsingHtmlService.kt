package com.example.voiceassistent

import android.os.Build
import androidx.annotation.RequiresApi
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.regex.Pattern

public class ParsingHtmlService {
    private val URL = "http://mirkosmosa.ru/holiday/2024"
    fun getHoliday(date: String?): String {
        var holidays = ""
        val days: ArrayList<String> = getDate(date)

        val document: Document = Jsoup.connect(URL).get()
        val body: Element = document.body()
        val daysOfMonth: Elements = body.select("#holiday_calend > div > div > div")

        for (dayInMonth in daysOfMonth) {
            for (day in days) {
                if (dayInMonth.getElementsByTag("span")[0].text().equals(day, ignoreCase = true)) {
                    val holiday = dayInMonth.getElementsByTag("a")
                    holidays += "$day : "
                    for (h in holiday) {
                        holidays += "${h.text()}, "
                    }
                    holidays = holidays.dropLast(2) + "\n"
                }
            }
        }
        return if (holidays.isBlank()) "Не знаю, как такое возможно..." else holidays.dropLast(1)
    }

    private fun getDate(inputDate: String?): ArrayList<String> {
        val dates = ArrayList<String>()
        var refinedInputDate = inputDate ?: ""

        val keywordsMappings = mapOf(
            "вчера" to LocalDate.now().minusDays(1),
            "сегодня" to LocalDate.now(),
            "завтра" to LocalDate.now().plusDays(1)
        )

        for ((keyword, date) in keywordsMappings) {
            if (refinedInputDate.contains(keyword, ignoreCase = true)) {
                dates.add(date.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("ru"))))
                refinedInputDate = refinedInputDate.replace(keyword, "", ignoreCase = true)
            }
        }

        val formattedDatePattern = Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{4})", Pattern.CASE_INSENSITIVE)
        var matcher = formattedDatePattern.matcher(refinedInputDate)
        while (matcher.find()) {
            val parsedDate = LocalDate.parse(matcher.group(1), DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            dates.add(parsedDate.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("ru"))))
            refinedInputDate = refinedInputDate.replace(matcher.group(1)!!, "")
            matcher = formattedDatePattern.matcher(refinedInputDate)
        }

        return dates
    }

}