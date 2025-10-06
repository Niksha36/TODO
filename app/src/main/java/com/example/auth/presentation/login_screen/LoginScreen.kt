package com.example.auth.presentation.login_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.core.presentation.ErrorDialog
import com.example.auth.presentation.common.CustomAuthTextField
import com.example.auth.presentation.common.PasswordTextField
import com.example.auth.presentation.common.SocialMediaButton
import com.example.todo.R
import com.example.app.ui.theme.Green
import com.example.app.ui.theme.TODOTheme

@Composable
fun LoginScreen(
    state: LoginScreenState,
    onEvent: (LoginScreenEvent) -> Unit,
    navigateToApp: () -> Unit
) {

    if (state.error != null) {
        ErrorDialog(
            message = state.error,
            onRetry = { onEvent(LoginScreenEvent.OnLoginClick) },
        )
    }
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    val scrollState = rememberScrollState()
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .clickable(indication = null, interactionSource = interactionSource) {
                focusManager.clearFocus()
            }
            .verticalScroll(scrollState)
    ) {
        Spacer(Modifier.weight(1f))
        Text("Login", style = MaterialTheme.typography.headlineLarge, color = Color.White)
        Column(Modifier.padding(top = 28.dp, bottom = 16.dp)) {
            Text(
                "Email",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            CustomAuthTextField(
                text = state.email,
                onTextChange = { onEvent(LoginScreenEvent.OnEmailChange(it)) },
                hint = "example@gmail.com",
                errorMessage = state.emailFieldError,
            )
        }
        Column(Modifier.padding(bottom = 24.dp)) {
            Text(
                "Password",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            PasswordTextField(
                password = state.password,
                onPasswordChange = { onEvent(LoginScreenEvent.OnPasswordChange(it)) },
            )
        }
        val isLoginButtonEnabled = state.isEmailCorrect == true && state.password.isNotEmpty()
        Button(
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Green.copy(alpha = 0.85f),
                contentColor = Color.White
            ),
            enabled = isLoginButtonEnabled,
            onClick = {
                onEvent(LoginScreenEvent.OnLoginClick)
            },
        ) {
            Text("Login", style = MaterialTheme.typography.labelLarge)
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp,CenterHorizontally),
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        ) {
            Text("Do not have an account?", style = MaterialTheme.typography.titleSmall, color = Color.White)
            Text(
                "Register",
                style = MaterialTheme.typography.titleSmall,
                color = Green,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .clickable { })
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 32.dp),
        )
        Column(Modifier.weight(2f)) {
            Row(
                modifier = Modifier
                    .height(40.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SocialMediaButton(
                    icon = R.drawable.tg_icon,
                    url = "https://vk.com",
                    color = Color(0xFF4285F4),
                    iconColor = Color.White,
                    modifier = Modifier.weight(1f, fill = true)
                )
                val gradientBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF141313),
                        Color.DarkGray,
                    ),
                    start = Offset(0f, 100f),
                    end = Offset(0f, 0f)
                )
                SocialMediaButton(
                    icon = R.drawable.github_ic,
                    url = "https://github.com/Niksha36?tab=repositories",
                    background = gradientBrush,
                    iconColor = Color.White,
                    modifier = Modifier.weight(1f, fill = true)
                )
            }
            if (state.isLoading) {
                Row(modifier = Modifier.fillMaxWidth().padding(top = 7.dp), horizontalArrangement = Arrangement.Center) {
                    CircularProgressIndicator()
                }
            }

        }


    }
    if (state.isLoginSuccess) {
        navigateToApp()
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    TODOTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            LoginScreen(
                state = LoginScreenState(
                    email = "test@example.com",
                    password = "ahdjfjh2jr2F",
                    isEmailCorrect = true,
                    isLoading = true
                ),
                onEvent = {},
                navigateToApp = {}
            )
        }
    }
}