package com.example.todo.presentation.utils

import android.util.Patterns
import androidx.compose.ui.graphics.Color
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object Utils {
    fun letterToColor(c: Char): Color {
        val palette = listOf(
            Color(0xFFEF5350),
            Color(0xFFAB47BC),
            Color(0xFF5C6BC0),
            Color(0xFF29B6F6),
            Color(0xFF26A69A),
            Color(0xFF66BB6A),
            Color(0xFFFFEE58),
            Color(0xFFFFA726),
            Color(0xFF78909C),
            Color(0xFF8D6E63),
            Color(0xFFFF7043),
            Color(0xFF7E57C2)
        )


        val code = c.uppercaseChar().code
        val idx = (code * 31) % palette.size
        return palette[idx]
    }
    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    fun formatTimestamp(timestamp: Timestamp): String {
        val messageCalendar = Calendar.getInstance().apply {
            time = timestamp.toDate()
        }
        val currentCalendar = Calendar.getInstance()

        val pattern = if (messageCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR)) {
            "dd MMM."
        } else {
            "dd MMM. yyyy"
        }
        val sdf = SimpleDateFormat(pattern, Locale.ENGLISH)
        return sdf.format(timestamp.toDate())
    }
}