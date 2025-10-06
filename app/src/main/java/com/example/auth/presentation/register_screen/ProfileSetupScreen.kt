package com.example.auth.presentation.register_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.app.ui.theme.Green
import com.example.app.ui.theme.TODOTheme
import com.example.auth.presentation.common.CustomAuthTextField
import com.example.core.presentation.ErrorDialog
import com.example.todo.R
import com.example.todo.presentation.common.CustomTopAppBar
import com.example.todo.presentation.main_screen.components.Avatar

@Composable
fun ProfileSetupScreen(
    onEvent: (RegisterScreenEvent) -> Unit,
    state: RegisterScreenState,
    navigateBack: () -> Unit,
    navigateToApp: () -> Unit
) {
    if (state.error != null) {
        ErrorDialog(
            message = state.error,
            onRetry = { onEvent(RegisterScreenEvent.OnRegisterClick) },
        )
    }
    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Register",
                showTitle = true,
                backgroundColor = Color.Transparent,
                contentColor = Color.White,
                onNavigateBackClick = navigateBack,
                height = 56.dp,
            )
        }
    ) { paddingValues ->
        val focusManager = LocalFocusManager.current
        val interactionSource = remember { MutableInteractionSource() }
        val scrollState = rememberScrollState()
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .clickable(indication = null, interactionSource = interactionSource) {
                    focusManager.clearFocus()
                }
                .verticalScroll(scrollState)
        ) {
            Spacer(Modifier.weight(0.5f))

            Image(
                painter = painterResource(R.drawable.ic_app_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .clip(RoundedCornerShape(70))
                    .background(Color.Green.copy(0.3f))
                    .padding(
                        10.dp
                    )
                    .size(50.dp)
            )
            Spacer(Modifier.weight(0.5f))
            Text(
                "Lets create your profile ✨",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 30.dp)
            )
            Row(
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .border(1.5.dp, Green, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Avatar(
                    imageUrl = state.avatarUrl,
                    name = state.name,
                    shape = CircleShape,
                    size = 85.dp
                )
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(start = 12.dp),
                ) {
                    CustomAuthTextField(
                        text = state.name,
                        hint = "Name",
                        onTextChange = { onEvent(RegisterScreenEvent.OnNameChange(it)) }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    CustomAuthTextField(
                        text = state.surname,
                        hint = "Surname",
                        onTextChange = { onEvent(RegisterScreenEvent.OnSurnameChange(it)) }
                    )
                }
            }
            val isRegisterButtonEnabled = state.name.isNotEmpty() && state.surname.isNotEmpty()
            Spacer(modifier = Modifier.height(30.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Green.copy(alpha = 0.85f),
                    contentColor = Color.White
                ),
                enabled = isRegisterButtonEnabled,
                onClick = {
                    onEvent(RegisterScreenEvent.OnRegisterClick)
                },
            ) {
                Text("Register", style = MaterialTheme.typography.labelLarge)
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 32.dp),
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Column(modifier = Modifier.width(IntrinsicSize.Min)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_app_logo),
                            contentDescription = "App Logo",
                            modifier = Modifier
                                .padding(end = 20.dp)
                                .size(50.dp)
                        )
                        Text(
                            text = "TaskSpace",
                            style = MaterialTheme.typography.headlineLarge
                        )
                    }
                    Box(
                        Modifier
                            .padding(top = 2.dp)
                            .fillMaxWidth()
                            .height(1.dp)
                            .background( DividerDefaults.color)
                    )
                }

            }


            Spacer(Modifier.weight(2f))
        }
    }
    if (state.isRegisterSuccess) {
        navigateToApp()
    }
}

@Preview
@Composable
fun ProfileSetupScreenPreview() {
    TODOTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ProfileSetupScreen(
                onEvent = {},
                state = RegisterScreenState(),
                navigateToApp = {},
                navigateBack = {}
            )
        }
    }
}