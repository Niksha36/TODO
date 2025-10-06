package com.example.todo.presentation.tasks_screen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app.ui.theme.Blue
import com.example.app.ui.theme.Green
import com.example.app.ui.theme.TODOTheme
import com.example.core.domain.model.User
import com.example.todo.domain.model.Task
import com.example.todo.presentation.main_screen.components.Avatar
import com.example.todo.presentation.utils.Utils


enum class Priority(val color: Color, val text: String) {
    LOW(Blue, "Low"),
    MEDIUM(Color.Yellow, "Medium"),
    HIGH(Color.Red, "High"),
    NONE(Color.Gray, "None")
}

enum class Status(val text: String, val color: Color) {
    TODO("TODO", Color.Gray),
    IN_PROGRESS("In progress", Blue),
    COMPLETED("Completed", Green)
}

@Composable
fun TaskCard(
    task: Task,
    openTaskDetailsBottomSheet: (Task) -> Unit,
    changeTaskStatus: (Task) -> Unit,
    onEditClick: (task: Task) -> Unit,
    onDeleteClick: (taskId: String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 2.5.dp, color = Color.White, shape = RoundedCornerShape(25.dp))
            .padding(17.dp, 12.dp)
            .height(IntrinsicSize.Min)
            .clickable { openTaskDetailsBottomSheet(task) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    task.title,
                    style = MaterialTheme.typography.titleMedium,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White,
                    maxLines = 2,
                )
            }
            //Task Tags
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                task.tags.forEach { tag ->
                    Box(
                        modifier = Modifier.background(
                            Utils.letterToColor(tag.first()).copy(0.65f),
                            RoundedCornerShape(20.dp)
                        )
                    ) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp)
                        )
                    }
                }
            }

            // Task Priority
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50f))
                    .background(task.priority.color.copy(0.3f))
                    .padding(vertical = 5.dp, horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Flag,
                    contentDescription = "Priority icon",
                    modifier = Modifier.size(24.dp),
                    tint = task.priority.color
                )

                Text(
                    text = task.priority.text,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                //Task Date
                Row {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.CalendarMonth,
                            contentDescription = "Stard date icon",
                            modifier = Modifier
                                .size(24.dp)
                                .padding(start = 12.dp),
                            tint = Color.White
                        )
                        Text(
                            text = Utils.formatTimestamp(task.createdAt),
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(start = 8.dp),
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))
                    task.deadline?.let { deadline ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.GpsFixed,
                                contentDescription = "Deadline icon",
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(start = 12.dp),
                                tint = Color.White
                            )

                            Text(
                                text = Utils.formatTimestamp(deadline),
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(start = 8.dp),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }


        Column(horizontalAlignment = Alignment.End) {
            if (task.status != Status.COMPLETED) {
                TaskDropDownMenu(
                    onEditClick = {
                        onEditClick(
                            task
                        )
                    },
                    onDeleteClick = {
                        onDeleteClick(
                            task.id
                        )
                    },
                    taskStatus = task.status
                ) {
                    Icon(
                        imageVector = Icons.Filled.MoreHoriz,
                        contentDescription = "More Options",
                        modifier = Modifier
                            .size(20.dp),
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            TaskActionButton(
                taskStatus = task.status,
                onClick = { changeTaskStatus(task) },
                taskPriority = task.priority
            )
            Spacer(modifier = Modifier.weight(1f))
            // Persons Assigned
            val avatarSize = 30.dp
            val overlap = 10.dp

            Row(
                modifier = Modifier.padding(start = 4.dp),
                horizontalArrangement = Arrangement.spacedBy((-overlap))
            ) {
                task.assignedTo.take(2).forEach { user ->
                    Avatar(
                        imageUrl = user.avatarUrl,
                        name = user.displayName,
                        shape = CircleShape,
                        size = avatarSize,
                        modifier = Modifier
                            .size(avatarSize)
                            .border(1.dp, Black, CircleShape)
                    )
                }

                if (task.assignedTo.size > 2) {
                    Box(
                        modifier = Modifier
                            .size(avatarSize)
                            .border(1.dp, Color.Black, CircleShape)
                            .background(Color.DarkGray, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "+${task.assignedTo.size - 2}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White
                        )
                    }
                }
            }
        }

    }


}

@Preview
@Composable
fun TaskCardPreview() {
    val task = Task(
        id = "1",
        projectId = "p1",
        owner = User(id = "u1", displayName = "Owner"),
        title = "Implement the new feature as discussed in the meeting",
        description = "A long description about what needs to be done for this particular task.",
        createdAt = com.google.firebase.Timestamp.now(),
        deadline = com.google.firebase.Timestamp.now(),
        status = Status.TODO,
        priority = Priority.HIGH,
        updatedAt = null,
        tags = listOf("Android", "Compose"),
        assignedTo = listOf(
            User(id = "u2", displayName = "John Doe", avatarUrl = null),
            User(id = "u3", displayName = "Jane Smith", avatarUrl = null),
            User(id = "u4", displayName = "Peter Jones", avatarUrl = null)
        )
    )
    TODOTheme {
        Box(
            modifier = Modifier
                .background(Color.DarkGray)
                .padding(16.dp)
        ) {
            TaskCard(
                task = task,
                openTaskDetailsBottomSheet = {},
                changeTaskStatus = {},
                onEditClick = {},
                onDeleteClick = {}
            )
        }
    }
}

@Composable
fun TaskActionButton(
    taskStatus: Status,
    onClick: () -> Unit,
    taskPriority: Priority
) {
    if (taskStatus == Status.TODO) {
        OutlinedButton(
            onClick = onClick,
            border = BorderStroke(1.dp, Green),
            colors = ButtonDefaults.buttonColors(
                containerColor = Green.copy(alpha = 0.25f),
                contentColor = Green,

                )
        ) {
            Text("Start")
        }
    } else if (taskStatus == Status.IN_PROGRESS) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clickable(onClick = onClick)
                .border(
                    width = 2.dp,
                    color = taskPriority.color,
                    shape = CircleShape
                )
                .background(taskPriority.color.copy(alpha = 0.3f), CircleShape)
        )
    }
}

//@Preview
//@Composable
//fun TaskCardPreview() {
//    val user1 = User(name = "John Doe", imageUrl = null)
//    val user2 = User(name = "Jane Smith", imageUrl = null)
//    val user3 = User(name = "Nikita Shurlo", imageUrl = null)
//    val task = task(
//        id = 1,
//        userId = 1,
//        title = "Complete the UI design for the new mobile app",
//        description = "A detailed description of the task.",
//        startDate = "Sep 30",
//        deadline = "Oct 5",
//        isCompleted = false,
//        status = Status.TODO,
//        priority = Priority.HIGH,
//        createdAt = "2023-10-25",
//        updatedAt = "2023-10-25",
//        tags = listOf("UI", "Design"),
//        assignedTo = listOf(user1, user2, user3)
//    )
//
//    TODOTheme {
//        Surface(color = MaterialTheme.colorScheme.background) {
//            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
//                TaskCard(task = task, onClick = {})
//                TaskCard(task = task, onClick = {})
//                TaskCard(task = task, onClick = {})
//            }
//        }
//    }
//
//}
