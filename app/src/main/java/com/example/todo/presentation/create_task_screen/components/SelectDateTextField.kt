package com.example.todo.presentation.create_task_screen.components

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.example.app.ui.theme.DardBlue
import com.example.app.ui.theme.Green
import com.example.todo.presentation.common.CollapsibleSection
import com.example.todo.presentation.create_task_screen.CustomTextField
import com.example.todo.presentation.utils.Utils
import com.google.firebase.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectDateTextField(
    dueDate: Timestamp?,
    onDateSelected: (Timestamp) -> Unit,
) {
    //Deadline
    var showDatePicker by remember { mutableStateOf(false) }
    Log.d("CreateTaskScreen", "Due Date: $dueDate")
    CollapsibleSection(title = "Due Date", initiallyExpanded = true) {
        CustomTextField(
            value = if (dueDate != null) Utils.formatTimestamp(dueDate) else "",
            onValueChange = {},
            hint = "Select date",
            leadingIcon = Icons.Default.CalendarMonth,
            readOnly = true,
            onClick = { showDatePicker = true }
        )

        if (showDatePicker) {
            val todayInUtc = remember {
                LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
            }

            val initialDateMillis = remember(dueDate) {
                dueDate?.toDate()?.time ?: todayInUtc
            }

            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = initialDateMillis,
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                        utcTimeMillis >= todayInUtc
                }
            )

            DatePickerDialog(
                colors = DatePickerDefaults.colors(
                    containerColor = DardBlue,
                    selectedDayContainerColor = Green,
                    selectedDayContentColor = DardBlue,
                    todayContentColor = Green,
                    todayDateBorderColor = Green,
                ),
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate()
                            val zonedDateTime = selectedDate.atStartOfDay(ZoneId.systemDefault())
                            onDateSelected(Timestamp(Date.from(zonedDateTime.toInstant())))
                        }
                        showDatePicker = false
                    }) { Text("OK", color = Green) }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text(
                            "Cancel",
                            color = Green
                        )
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState,
                    colors = DatePickerDefaults.colors(
                        containerColor = DardBlue,
                        titleContentColor = Color.White,
                        headlineContentColor = Green,
                        subheadContentColor = Color.White,
                        weekdayContentColor = Color.White,
                        yearContentColor = Color.White,
                        currentYearContentColor = Green,
                        selectedYearContentColor = DardBlue,
                        selectedYearContainerColor = Green,
                        dayContentColor = Color.White,
                        disabledDayContentColor = Color.Gray,
                        selectedDayContainerColor = Green,
                        selectedDayContentColor = DardBlue,
                        todayContentColor = Green,
                        todayDateBorderColor = Green,
                    )
                )
            }
        }
    }
}