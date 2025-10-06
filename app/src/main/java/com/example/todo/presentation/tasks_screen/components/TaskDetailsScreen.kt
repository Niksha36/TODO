package com.example.todo.presentation.tasks_screen.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Subject
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.GpsFixed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app.ui.theme.TODOTheme
import com.example.core.domain.model.User
import com.example.todo.domain.model.Task
import com.example.todo.presentation.common.CollapsibleSection
import com.example.todo.presentation.common.TagsRow
import com.example.todo.presentation.main_screen.components.Avatar
import com.example.todo.presentation.utils.Utils
import com.google.firebase.Timestamp

@Composable
fun TaskDetailsScreen(
    task: Task,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {


    Column(verticalArrangement = Arrangement.spacedBy(15.dp)) {
        Surface(
            shape = RoundedCornerShape(15.dp),
            color = task.status.color,
        ) {
            Text(
                text = task.status.text.uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp)
            )
        }
        //task title and change status button
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            TaskActionButton(
                taskStatus = task.status,
                onClick = {},
                taskPriority = task.priority
            )
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )

            TaskDropDownMenu(
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick,
                taskStatus = task.status
            ) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "More",
                    tint = Color.White
                )
            }
        }
        // Description
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Filled.Subject,
                contentDescription = "Description Icon",
                tint = Color.White
            )
            Text(
                text = task.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
        // Task date info
        Row(
            horizontalArrangement = Arrangement.spacedBy(30.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.DateRange,
                    contentDescription = "Creation Date",
                    tint = Color.White
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = Utils.formatTimestamp(task.createdAt),
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White
                )
            }
            task.deadline?.let { deadline ->

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.GpsFixed,
                        contentDescription = "Deadline",
                        tint = Color.Red
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = Utils.formatTimestamp(deadline),
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White
                    )
                }
            }
        }
        //Priority
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Flag,
                contentDescription = "Creation Date",
                tint = task.priority.color
            )
            Text(
                text = task.priority.text,
                style = MaterialTheme.typography.titleSmall,
                color = Color.White
            )
        }
        // Tags
        TagsRow(tags = task.tags)
        // Assignees
        CollapsibleSection(title = "Assignees", leadingIcon = Icons.Default.Person) {
            if (task.assignedTo.isEmpty()) {
                Text(
                    text = "No assignees",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray,
                    modifier = Modifier.padding(vertical = 5.dp)
                )
            } else {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    task.assignedTo.forEach { user ->
                        Row(
                            modifier = Modifier
                                .border(
                                    1.dp, Color.LightGray, RoundedCornerShape(30.dp)
                                )
                                .padding(vertical = 5.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        )
                        {
                            Avatar(
                                imageUrl = user.avatarUrl,
                                name = user.displayName,
                                shape = CircleShape,
                                size = 30.dp,
                            )
                            Text(
                                text = user.displayName,
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.White,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                    }
                }

            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1E1E)
@Composable
private fun TaskDetailsScreenPreview() {
    val user1 =
        User(id = "1", email = "john@example.com", displayName = "John Doe", avatarUrl = null)
    val user2 = User(id = "2", email = "alice@example.com", displayName = "Alice", avatarUrl = null)
    val task = Task(
        id = "taskId1",
        projectId = "proj1",
        owner = user1,
        title = "Implement Authentication",
        description = "Set up Firebase authentication with email and password. Ensure secure login and registration flows. " +
                "\n\nAlso, integrate social logins if possible.",
        createdAt = Timestamp.now(),
        deadline = null,
        status = Status.IN_PROGRESS,
        priority = Priority.HIGH,
        updatedAt = null,
        tags = listOf("Android", "Firebase", "Backend", "Spring", "JWT"),
        assignedTo = listOf(user1, user2)
    )

    TODOTheme {
        Surface {
            TaskDetailsScreen(task = task, onEditClick = {}, onDeleteClick = {})
        }
    }

}