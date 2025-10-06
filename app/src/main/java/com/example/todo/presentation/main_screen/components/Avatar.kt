package com.example.todo.presentation.main_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraEnhance
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.todo.presentation.utils.Utils

@Composable
fun Avatar(
    imageUrl: String? = null,
    name: String? = null,
    shape: RoundedCornerShape = CircleShape,
    size: Dp,
    modifier: Modifier = Modifier
) {
    if (!imageUrl.isNullOrEmpty()) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Avatar",
            modifier = Modifier
                .size(size)
                .clip(shape),
            contentScale = ContentScale.Crop
        )
    } else {
        if (!name.isNullOrEmpty()){
            val letter = name.first().uppercaseChar()
            Box(
                modifier = modifier
                    .size(size)
                    .clip(shape)
                    .background(Utils.letterToColor(letter).copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = letter.toString(),
                    color = Color.White,
                    fontSize = (size.value / 2).sp
                )
            }
        } else {
            Box(
                modifier = modifier
                    .size(size)
                    .clip(shape)
                    .background(Color.Gray.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraEnhance,
                    contentDescription = "No Avatar",
                    tint = Color.White.copy(alpha = 0.7f),
                )
            }
        }

    }
}