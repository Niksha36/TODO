package com.example.auth.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.ui.theme.Green
import com.example.app.ui.theme.LightGrey

@Composable
fun CustomAuthTextField(
    modifier: Modifier = Modifier,
    text: String,
    hint: String? = null,
    onTextChange: (String) -> Unit,
    errorMessage: String? = null
) {
    val isError = errorMessage != null
    Column {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(42.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(
                    color = if (isError) Color.Red.copy(alpha = 0.5f) else LightGrey,
                    shape = RoundedCornerShape(30.dp)
                ),
        ) {
            BasicTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                singleLine = true,
                textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                cursorBrush = SolidColor(Green)
            )
            if (text.isEmpty() && hint != null) {
                Text(
                    text = hint,
                    color = Color.White.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    textAlign = TextAlign.Start
                )
            }
        }
        if (errorMessage != null){
            Text(errorMessage, color = Color.Red, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp, top = 4.dp))
        }
    }

}
@Composable
@Preview
fun EmailTextFieldPreview() {
    Box(Modifier.size(300.dp).background(Color.White)) {
        CustomAuthTextField(
            text = "",
            hint = "example@gmail.com",
            onTextChange = {},
        )
    }
}