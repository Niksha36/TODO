package com.example.todo.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DotsIndicator(
    currentPage: Int,
    totalPages: Int,
    rowPadding: PaddingValues = PaddingValues(top = 10.dp, bottom = 10.dp),
    dotSize: Dp = 8.dp,
    useRow: Boolean = true,
    selectedColor: Color = Color.White,
    unselectedColor: Color = Color.Gray,
){
    if(useRow) {
        Row(modifier = Modifier.padding(paddingValues = rowPadding).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)) {
            repeat(totalPages) { index ->
                Box(
                    modifier = Modifier
                        .size(dotSize)
                        .background(
                            color = if (index == currentPage) selectedColor else unselectedColor,
                            shape = CircleShape
                        )
                )
            }
        }
    }  else{
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            repeat(totalPages) { index ->
                Box(
                    modifier = Modifier
                        .size(dotSize)
                        .background(
                            color = if (index == currentPage) selectedColor else unselectedColor,
                            shape = CircleShape
                        )
                )
            }
        }
    }
}