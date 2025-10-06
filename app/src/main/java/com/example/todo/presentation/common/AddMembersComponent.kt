package com.example.todo.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.ui.theme.Green
import com.example.app.ui.theme.LightGrey
import com.example.core.domain.model.User
import com.example.todo.presentation.create_task_screen.CustomTextField
import com.example.todo.presentation.main_screen.components.Avatar

@Composable
fun AddMembersContent(
    members: List<User>,
    isLoading: Boolean,
    error: String,
    addUserToProject: (String) -> Unit,
    onRemoveMember: (User) -> Unit
) {
    var memberEmail by remember { mutableStateOf("") }
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                CustomTextField(
                    value = memberEmail,
                    onValueChange = { memberEmail = it },
                    hint = "User email"
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (memberEmail.isNotBlank()) {
                        addUserToProject(memberEmail)
                        memberEmail = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Green),
                enabled = memberEmail.isNotBlank() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.Black,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Find Member",
                        tint = Color.Black
                    )
                }
            }
        }

        if (error.isNotEmpty()) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (members.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 150.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(members) { member ->
                    MemberListItem(user = member) {
                        onRemoveMember(member)
                    }
                }
            }
        }
    }
}

@Composable
private fun MemberListItem(user: User, onRemove: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = LightGrey.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Avatar(name = user.displayName, imageUrl = user.avatarUrl, size = 32.dp)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = user.displayName,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp,
            )
            IconButton(onClick = onRemove, modifier = Modifier.padding(start = 20.dp).size(20.dp)) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove member",
                    tint = Color.Gray
                )
            }
        }
    }
}