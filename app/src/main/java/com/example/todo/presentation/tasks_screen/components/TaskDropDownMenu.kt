package com.example.todo.presentation.tasks_screen.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

@Composable
fun TaskDropDownMenu(
    taskStatus: Status,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    var showActions by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { showActions = true }) {
            icon()
        }
        DropdownMenu(
            expanded = showActions,
            onDismissRequest = { showActions = false }
        ) {
            if (taskStatus != Status.COMPLETED){
                DropdownMenuItem(
                    text = { Text("Edit task") },
                    onClick = {
                        onEditClick()
                        showActions = false
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Edit,
                            contentDescription = "Edit task"
                        )
                    }
                )
            }
            if (taskStatus == Status.TODO){
                DropdownMenuItem(
                    text = { Text("Delete task", color = Color.Red.copy(alpha = 0.8f)) },
                    onClick = {
                        onDeleteClick()
                        showActions = false
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Delete,
                            tint = Color.Red.copy(alpha = 0.8f),
                            contentDescription = "Delete task"
                        )
                    }
                )
            }

        }
    }
}