package com.example.todo.presentation.main_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.app.ui.theme.Green
import com.example.app.ui.theme.LightGrey
import com.example.core.domain.model.User
import com.example.todo.presentation.common.AddMembersContent
import com.example.todo.presentation.common.CollapsibleSection
import com.example.todo.presentation.create_task_screen.CustomTextField
import com.example.todo.presentation.main_screen.CreateProjectDialogEvent
import com.example.todo.presentation.main_screen.CreateProjectDialogState

@Composable
fun CreateProjectDialog(
    state: CreateProjectDialogState,
    onEvent: (CreateProjectDialogEvent) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF161b22),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = "Create Project",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Project Name",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                CustomTextField(
                    value = state.name,
                    onValueChange = { onEvent(CreateProjectDialogEvent.OnProjectNameChange(it)) },
                    hint = "Enter project name"
                )
                Spacer(modifier = Modifier.height(16.dp))

                CollapsibleSection(
                    title = "Add Members",
                    leadingIcon = Icons.Default.GroupAdd
                ) {
                    AddMembersContent(
                        addUserToProject = { email ->
                            onEvent(CreateProjectDialogEvent.AddUserToProject(email))
                        },
                        onRemoveMember = { user ->
                            onEvent(CreateProjectDialogEvent.OnRemoveMember(user))
                        },
                        members = state.members,
                        isLoading = state.isLoading,
                        error = state.error,
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { onEvent(CreateProjectDialogEvent.CreateProject) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Green,
                        contentColor = Color.Black,
                        disabledContainerColor = Green.copy(alpha = 0.5f)
                    ),
                    enabled = state.name.isNotBlank(),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Text("Create Project", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Preview
@Composable
fun CreateProjectDialogPreview() {
    val state = CreateProjectDialogState(
        name = "My Awesome Project",
        members = listOf(User(id = "1", email = "test@example.com", displayName = "John Doe", avatarUrl = null)),
        isLoading = false,
        error = ""
    )
    CreateProjectDialog(state = state, onEvent = {}, onDismiss = {})
}
