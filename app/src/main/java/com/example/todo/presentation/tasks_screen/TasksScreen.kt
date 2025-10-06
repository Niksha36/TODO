package com.example.todo.presentation.tasks_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.app.ui.theme.Green
import com.example.todo.presentation.common.CustomTopAppBar
import com.example.todo.presentation.tasks_screen.components.TaskDetailsScreen
import com.example.todo.presentation.tasks_screen.components.TodoistBottomSheet
import com.example.todo.presentation.tasks_screen.components.Status
import com.example.todo.presentation.tasks_screen.components.TaskCard

@Composable
fun TasksScreen(
    navigateBack: () -> Unit,
    state: TasksScreenState,
    navigateToCreateTaskScreen: (projectId: String) -> Unit,
    navigateToEditTaskScreen: (projectId: String, task: String) -> Unit,
    onEvent: (TasksScreenEvent) -> Unit,
) {
    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Tasks",
                showTitle = true,
                backgroundColor = Color.Transparent,
                contentColor = Color.White,
                onNavigateBackClick = navigateBack,
                height = 56.dp,
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 12.dp)
                .padding(top = 18.dp)
        ) {
            if (state.project?.name != null) {
                Text(
                    text = "\uD83D\uDDC2\uFE0F ${state.project.name}",
                    style = MaterialTheme.typography.displayMedium,
                    color = Color.White
                )
            } else {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(top = 50.dp)
                        .size(30.dp)
                        .align(Alignment.CenterHorizontally),
                    color = Color.White,
                    strokeWidth = 3.dp
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = 25.dp)
                    .fillMaxWidth()
            ) {
                //Add button
                val buttonEnabled = state.project != null
                Button(
                    onClick = {
                        if (buttonEnabled) {
                            navigateToCreateTaskScreen(state.project.id)
                        }
                    },
                    modifier = Modifier.border(1.5.dp, Green, CircleShape),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    enabled = buttonEnabled,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Back",
                        tint = Color.Black,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Green)
                            .padding(8.dp)
                            .size(20.dp)
                    )

                    Text(
                        "Add Task",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                //Filter button
                IconButton(
                    onClick = {},
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filter",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            val statuses = remember { Status.entries }
            val pagerState = rememberPagerState(
                initialPage = statuses.indexOf(state.statusTabToOpen),
                pageCount = { statuses.size }
            )


            // Page indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                statuses.forEachIndexed { index, _ ->
                    val isSelected = index == pagerState.currentPage
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(if (isSelected) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) Green else Color.Gray)
                    )
                }
            }
            // Horizontal pager for task statuses
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Top,
                contentPadding = PaddingValues(horizontal = 5.dp),
                pageSpacing = 10.dp
            ) { page ->
                val status = statuses[page]
                val statusTasks = state.projectsByStatus[status] ?: emptyList()
                Column {
                    Text(
                        "${status.text} (${statusTasks.size})",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(statusTasks) { task ->
                            TaskCard(
                                task = task,
                                openTaskDetailsBottomSheet = {
                                    onEvent(
                                        TasksScreenEvent.OpenTaskDetailsBottomSheet(
                                            task
                                        )
                                    )
                                },
                                changeTaskStatus = { onEvent(TasksScreenEvent.ChangeTaskStatus(task)) },
                                onEditClick = {
                                    navigateToEditTaskScreen(state.project!!.id, task.id)
                                },
                                onDeleteClick = { onEvent(TasksScreenEvent.DeleteTask(task.id)) }
                            )
                        }
                    }
                }
            }
        }
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()) {
        TodoistBottomSheet(
            visible = state.taskDetailsToShow != null,
            onDismissRequest = { onEvent(TasksScreenEvent.CloseTaskDetailsBottomSheet) },
        ) {
            state.taskDetailsToShow?.let { TaskDetailsScreen(task = it, {}, {}) }
        }
    }
}

//@Preview
//@Composable
//fun TaskScreenPreview() {
//    val user1 = User(name = "Mary Jane", imageUrl = null)
//    val user2 = User(name = "Jane Smith", imageUrl = null)
//    val task1 = Task(
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
//        assignedTo = listOf(user1, user2)
//    )
//    val task2 = Task(
//        id = 2,
//        userId = 1,
//        title = "Implement the new feature",
//        description = "A detailed description of the feature.",
//        startDate = "Oct 1",
//        deadline = "Oct 10",
//        isCompleted = false,
//        status = Status.IN_PROGRESS,
//        priority = Priority.MEDIUM,
//        createdAt = "2023-10-25",
//        updatedAt = "2023-10-25",
//        tags = listOf("Backend", "Feature"),
//        assignedTo = listOf(user1)
//    )
//    TODOTheme {
//        Surface(color = MaterialTheme.colorScheme.background) {
//            TasksScreen(
//                projectName = "Mobile App Development",
//                navigateBack = {},
//                taskList = listOf(task1, task2)
//            )
//        }
//    }
//
//}