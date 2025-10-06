package com.example.todo.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.todo.presentation.utils.Utils

@Composable
fun TagsRow(tags: List<String>, onDelete: ((String) -> Unit)? = null, modifier: Modifier = Modifier) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = modifier) {
        items(tags) { tag ->
            Box(
                modifier = Modifier
                    .background(
                        Utils.letterToColor(tag.first()).copy(0.65f),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(vertical = 5.dp, horizontal = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Label,
                        contentDescription = "Tag Icon",
                        tint = Color.White,
                        modifier = Modifier.rotate(-45f)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = tag,
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp)
                    )
                    if (onDelete != null){
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tag Icon",
                            tint = Color.White,
                            modifier = Modifier
                                .padding(7.dp)
                                .clip(CircleShape)
                                .background(Color.Gray.copy(alpha = 0.5f))
                                .size(15.dp)
                                .padding(3.dp)
                                .clickable { onDelete(tag) }
                        )
                    }

                }
            }
        }
    }
}
