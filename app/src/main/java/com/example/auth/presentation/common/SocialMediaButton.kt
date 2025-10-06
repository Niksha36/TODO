// Язык: Kotlin
package com.example.auth.presentation.common

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todo.R

@Composable
fun SocialMediaButton(
    icon: Int,
    url: String,
    background: Brush,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Button(
        onClick = {
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(context, Uri.parse(url))
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = ButtonDefaults.ContentPadding,
        modifier = modifier
            .background(background, shape = ButtonDefaults.shape)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.height(26.dp)
        )
    }

}

@Composable
fun SocialMediaButton(
    icon: Int,
    url: String,
    color: Color,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    SocialMediaButton(
        icon = icon,
        url = url,
        background = Brush.linearGradient(listOf(color, color)),
        iconColor = iconColor,
        modifier = modifier
    )
}


@Preview
@Composable
fun SocialMediaButtonPreview() {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.height(40.dp)) {
        SocialMediaButton(
            icon = R.drawable.tg_icon,
            url = "https://vk.com",
            color = Color(0xFF4285F4),
            iconColor = Color.White,
            modifier = Modifier.weight(1f, fill = true)
        )
        val gradientBrush = Brush.linearGradient(
            colors = listOf(
                Color.Black,
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
}