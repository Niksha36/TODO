package com.example.todo.presentation.main_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app.ui.theme.Blue
import com.example.app.ui.theme.Green
import com.example.app.ui.theme.TODOTheme
import com.example.core.domain.model.User
import com.example.core.presentation.ErrorDialog
import com.example.todo.R
import com.example.todo.presentation.main_screen.components.Avatar
import com.example.todo.presentation.main_screen.components.CreateProjectDialog
import com.example.todo.presentation.main_screen.components.StatusCard
import com.example.todo.presentation.tasks_screen.components.Status
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    onEvent: (MainScreenEvent) -> Unit,
    state: MainScreenState = MainScreenState(),
    navigateToTasks: (status: Status, projectId: String) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val user = state.currentUser
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(225.dp),
                drawerContainerColor = Color(0xFF0d1117),
                drawerContentColor = Color.White,
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        item {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Projects",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier

                                )
                                Spacer(modifier = Modifier.weight(1f))


                                IconButton(
                                    onClick = { onEvent(MainScreenEvent.ToggleCreateProjectDialog) },
                                    modifier = Modifier
                                        .size(width = 70.dp, height = 35.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(Color.Green)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add icon",
                                        tint = Color.Black,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                        }
                        items(state.projects) { project ->
                            Box(
                                modifier = Modifier
                                    .clickable {
                                        onEvent(MainScreenEvent.SelectProject(project))
                                        scope.launch { drawerState.close() }
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(15.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Avatar(
                                        name = project.name,
                                        shape = RoundedCornerShape(15.dp),
                                        size = 40.dp
                                    )
                                    Text(
                                        text = project.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        modifier = Modifier
                                            .padding(start = 16.dp),
                                        maxLines = 2,
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    IconButton(
                                        onClick = {onEvent(MainScreenEvent.DeleteProject(project))}
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Delete,
                                            contentDescription = "Delete Project",
                                            tint = Color.Red.copy(0.7f),
                                        )
                                    }
                                }

                            }

                        }
                    }
                    if (state.isLoadingProjects) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f))
                                .clickable(enabled = false, onClick = {}),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(70.dp),
                                color = Green
                            )
                        }
                    }
                }
            }

        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .padding(top = 7.dp)
                ) {
                    IconButton(
                        onClick = { scope.launch { drawerState.open() } },
                        modifier = Modifier
                            .size(50.dp)
                            .background(
                                color = Green.copy(alpha = 0.15f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Меню",
                            tint = Green,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    user?.let {
                        var showMenu by remember { mutableStateOf(false) }
                        Box {
                            Avatar(
                                imageUrl = it.avatarUrl,
                                name = it.displayName,
                                size = 50.dp,
                                modifier = Modifier.clickable { showMenu = true }
                            )
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false },
                                modifier = Modifier.background(Color(0xFF161b22))
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Email,
                                                contentDescription = "Email",
                                                tint = Color.White
                                            )
                                            Text(it.email, color = Color.White)
                                        }
                                    },
                                    onClick = {}
                                )
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                                contentDescription = "Выход",
                                                tint = Color.Red
                                            )
                                            Text("Выход", color = Color.Red)
                                        }
                                    },
                                    onClick = {
                                        showMenu = false
                                        onEvent(MainScreenEvent.Logout)
                                    }
                                )
                            }
                        }
                    } ?: Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(
                                color = Color.Gray.copy(alpha = 0.15f),
                                shape = CircleShape
                            )
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(30.dp)
                                .align(Alignment.Center),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    }
                }

                if (state.currentProject != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "🗂️ ${state.currentProject.name}",
                            style = MaterialTheme.typography.displayMedium,
                            color = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Column {
                            StatusCard(
                                modifier = Modifier.offset(y = 62.dp),
                                title = "TODO",
                                count = state.countOfTasksByStatus[Status.TODO].toString(),
                                color = Color.LightGray,
                                icon = painterResource(R.drawable.todo_icon24),
                                onClick = {
                                    state.currentUser?.let {
                                        navigateToTasks(Status.TODO, state.currentProject.id)
                                    }
                                }
                            )
                            StatusCard(
                                modifier = Modifier.offset(y = 42.dp),
                                title = "In Progress",
                                count = state.countOfTasksByStatus[Status.IN_PROGRESS].toString(),
                                color = Blue,
                                textColor = Color.White,
                                icon = rememberVectorPainter(Icons.Outlined.Circle),
                                onClick = {
                                    state.currentUser?.let {
                                        navigateToTasks(Status.IN_PROGRESS, state.currentProject.id)
                                    }
                                }
                            )
                            StatusCard(
                                modifier = Modifier.offset(y = 20.dp),
                                title = "Completed",
                                count = state.countOfTasksByStatus[Status.COMPLETED].toString(),
                                color = Green,
                                icon = rememberVectorPainter(Icons.Outlined.Check),
                                onClick = {
                                    state.currentUser?.let {
                                        navigateToTasks(Status.COMPLETED, state.currentProject.id)
                                    }
                                }
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 32.dp)
                            .clickable { scope.launch { drawerState.open() } },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_app_logo),
                                contentDescription = "logo",
                                tint = Green,
                                modifier = Modifier.size(72.dp)
                            )
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Select or create a project",
                                    color = Color.White,
                                    style = MaterialTheme.typography.headlineSmall,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "Manage your tasks and stay organized.",
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    )

    if (state.isCreateProjectDialogVisible) {
        CreateProjectDialog(
            onDismiss = { onEvent(MainScreenEvent.ToggleCreateProjectDialog) },
            state = state.createProjectDialogState,
            onEvent = { onEvent(MainScreenEvent.OnCreateProjectDialogEvent(it)) },
        )
    }

    when {
        state.isLoading -> {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(70.dp)
                        .align(Alignment.Center),
                    color = Green
                )
            }
        }

        state.error.isNotEmpty() -> {
            ErrorDialog(
                message = state.error,
            )
        }
    }
}

@Preview
@Composable
fun TaskStatusScreenPreview() {
    val user = User(
        id = "1",
        displayName = "John Doe",
        email = "",
        avatarUrl = null
    )
    TODOTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            MainScreen(
                onEvent = {},
                state = MainScreenState(currentUser = user, isLoading = false),
                navigateToTasks = { _, _ -> }
            )
        }

    }
}