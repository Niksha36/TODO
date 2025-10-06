package com.example.todo.presentation.create_task_screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.NewLabel
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app.ui.theme.Green
import com.example.app.ui.theme.TODOTheme
import com.example.core.presentation.ErrorDialog
import com.example.todo.presentation.common.AddMembersContent
import com.example.todo.presentation.common.CollapsibleSection
import com.example.todo.presentation.common.CustomTopAppBar
import com.example.todo.presentation.common.TagsRow
import com.example.todo.presentation.create_task_screen.components.SelectDateTextField
import com.example.todo.presentation.tasks_screen.components.Priority

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    navigateBack: () -> Unit,
    state: CreateTaskScreenState,
    onEvent: (CreateTaskScreenEvent) -> Unit,
) {
    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = if (!state.isEditing) "Create Task" else "Edit Task",
                showTitle = true,
                backgroundColor = Color.Transparent,
                contentColor = Color.White,
                onNavigateBackClick = navigateBack,
                height = 56.dp,
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        if (state.isLoading) {
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
        } else if (state.error.isNotBlank()) {
            ErrorDialog(state.error)
        } else {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 12.dp)
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = "Task Name",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
                )

                CustomTextField(
                    value = state.taskName,
                    onValueChange = { onEvent(CreateTaskScreenEvent.OnTaskNameChange(it)) },
                    hint = "Enter task name"
                )

                Text(
                    text = "Task Description",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
                )

                CustomTextField(
                    value = state.description,
                    onValueChange = { onEvent(CreateTaskScreenEvent.OnDescriptionChange(it)) },
                    hint = "Write description here",
                    minLines = 2,
                    shape = RoundedCornerShape(15.dp)
                )

                //Priority
                Text(
                    text = "Priority",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(Priority.entries) { priority ->
                        val isSelected = state.priority == priority
                        val alpha = if (isSelected) 1f else 0.5f

                        Button(
                            onClick = { onEvent(CreateTaskScreenEvent.OnPriorityChange(priority)) },
                            shape = RoundedCornerShape(30.dp),
                            border = BorderStroke(1.5.dp, priority.color.copy(alpha = alpha)),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) priority.color.copy(alpha = 0.5f) else Color.Transparent,
                                contentColor = priority.color.copy(alpha = alpha)
                            ),
                        ) {
                            Text(text = priority.text)
                        }
                    }
                }

                //Responsible Person
                CollapsibleSection(title = "Responsible Person") {
                    AddMembersContent(
                        members = state.responsiblePersons,
                        isLoading = state.isSearchingUser,
                        error = state.searchingUserError,
                        addUserToProject = { email ->
                            onEvent(
                                CreateTaskScreenEvent.OnResponsiblePersonAdd(
                                    email
                                )
                            )
                        },
                        onRemoveMember = { user ->
                            onEvent(
                                CreateTaskScreenEvent.OnResponsiblePersonRemove(
                                    user
                                )
                            )
                        },
                    )
                }

                //Deadline
                SelectDateTextField(
                    dueDate = state.deadline,
                    onDateSelected = { onEvent(CreateTaskScreenEvent.OnDeadlineChange(it)) }
                )

                // Tags
                CollapsibleSection(title = "Tags") {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CustomTextField(
                                value = state.tag,
                                onValueChange = {
                                    onEvent(CreateTaskScreenEvent.OnTagChange(it))
                                },
                                hint = "Write tag here",
                                leadingIcon = Icons.Filled.NewLabel,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { onEvent(CreateTaskScreenEvent.OnTagAdd) },
                                modifier = Modifier
                                    .padding(start = 5.dp)
                                    .align(Alignment.CenterVertically),
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = Green,
                                    contentColor = Color.Black
                                ),
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Default.ArrowForward,
                                    contentDescription = "Add Tag",
                                    modifier = Modifier.padding(3.dp)
                                )
                            }
                        }

                        TagsRow(
                            tags = state.tags,
                            onDelete = { tag -> onEvent(CreateTaskScreenEvent.OnTagRemove(tag)) },
                            modifier = Modifier.padding(top = 15.dp)
                        )

                    }

                }
                Button(
                    onClick = {
                        if (state.isEditing) onEvent(CreateTaskScreenEvent.UpdateTask) else onEvent(
                            CreateTaskScreenEvent.SubmitTask
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    enabled = if (state.isEditing) state.isModified else true,
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.Black,
                        containerColor = Color.Green,
                        disabledContainerColor = Color.Gray
                    ),
                    contentPadding = PaddingValues(
                        vertical = 12.dp
                    )
                ) {
                    Text(
                        if (!state.isEditing) "Create Task" else "Save Changes",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }
        if (state.isTaskAddedToProject) {
            navigateBack()
        }
    }
}


@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    hint: String = "",
    leadingIcon: ImageVector? = null,
    minLines: Int = 1,
    shape: Shape = RoundedCornerShape(30.dp),
    readOnly: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            leadingIcon = if (leadingIcon != null) {
                {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = "Leading Icon",
                    )
                }
            } else {
                null
            },
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(hint) },
            shape = shape,
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                cursorColor = Color.White,
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color.Gray,
                unfocusedPlaceholderColor = Color.Gray,
                focusedPlaceholderColor = Color.Gray,
                focusedLeadingIconColor = Color.White,
                unfocusedLeadingIconColor = Color.Gray
            ),
            minLines = minLines,
            readOnly = readOnly,
        )

        if (onClick != null) {
            val interaction = remember { MutableInteractionSource() }
            // Overlay to catch clicks across the whole field and open date picker
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(indication = null, interactionSource = interaction) {
                        onClick()
                    }
            )
        }
    }
}

@Preview
@Composable
fun CreateTaskScreenPreview() {
    TODOTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            CreateTaskScreen(
                navigateBack = {},
                state = CreateTaskScreenState(tags = listOf("Design", "Urgent")),
                onEvent = {}
            )

        }
    }
}


@Preview
@Composable
fun CustomTextFieldPreview() {
    TODOTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            var text by remember { mutableStateOf("") }
            CustomTextField(
                value = text,
                onValueChange = { text = it },
                hint = "Enter task name"
            )
        }
    }
}