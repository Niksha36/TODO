package com.example.todo.presentation.tasks_screen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.app.ui.theme.TODOTheme
import com.example.core.domain.model.User
import com.example.todo.domain.model.Task
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Composable
fun TodoistBottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 22.dp,
    closeThresholdFractionOfContent: Float = 0.5f, // drag down > 50% of content height to close
    expandToTopTriggerFractionOfScreen: Float = 0.15f, // drag up into top 15% of screen to expand
    collapseFromTopTriggerFractionOfScreen: Float = 0.2f, // release above 20% keeps expanded
    scrimColor: Color = Color.Black.copy(alpha = 0.35f),
    content: @Composable BoxScope.() -> Unit,
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val windowInfo = LocalWindowInfo.current
    val containerHeightPx = windowInfo.containerSize.height.toFloat()

    val screenHeightWithInsetsPx = containerHeightPx

    var contentHeightPx by remember { mutableFloatStateOf(0f) }
    val maxSheetHeightPx = screenHeightWithInsetsPx * 0.95f

    // Animated top offset (in px) from the top of the screen.
    val offsetY = remember { Animatable(screenHeightWithInsetsPx) }


    var expanded by remember { mutableStateOf(false) }
    var dragging by remember { mutableStateOf(false) }
    // New: settling state to smoothly follow animation after drag end
    var settling by remember { mutableStateOf(false) }

    // Compute key anchor positions
    val collapsedHeightPx = remember(contentHeightPx, maxSheetHeightPx) {
        if (contentHeightPx <= 0f) 0f else minOf(contentHeightPx, maxSheetHeightPx)
    }
    val collapsedY = remember(screenHeightWithInsetsPx, collapsedHeightPx) {
        (screenHeightWithInsetsPx - collapsedHeightPx).coerceAtLeast(0f)
    }

    // Open/close reactions
    LaunchedEffect(visible, collapsedY) {
        if (visible) {
            if (offsetY.value >= screenHeightWithInsetsPx - 1f) {
                offsetY.snapTo(screenHeightWithInsetsPx)
            }
            expanded = false
            if (collapsedY > 0f) {
                offsetY.animateTo(
                    targetValue = collapsedY,
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                )
            }
        } else {
            offsetY.animateTo(
                targetValue = screenHeightWithInsetsPx,
                animationSpec = tween(durationMillis = 220)
            )
            expanded = false
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(scrimColor)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { onDismissRequest() })
                }
        ) {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .graphicsLayer { translationY = offsetY.value }
                    .align(Alignment.TopStart)
            ) {
                Surface(
                    tonalElevation = 3.dp,
                    shape = RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius),
                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                    modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap = { /* consume */ }) }
                ) {
                    val currentHeightPx = when {
                        expanded -> screenHeightWithInsetsPx
                        else -> (screenHeightWithInsetsPx - offsetY.value).coerceAtLeast(0f).coerceAtMost(maxSheetHeightPx)
                    }

                    val heightModifier = when {
                        expanded -> Modifier.height(with(LocalDensity.current) { currentHeightPx.toDp() })
                        dragging || settling -> {
                            val base = max(collapsedHeightPx, currentHeightPx)
                            Modifier.height(with(LocalDensity.current) { base.toDp() })
                        }
                        else -> Modifier.heightIn(max = with(LocalDensity.current) { maxSheetHeightPx.toDp() })
                    }

                    Box(
                        modifier = heightModifier
                            .onGloballyPositioned { coords ->
                                val newMeasuredHeight = coords.size.height.toFloat()
                                val prevCollapsedY = (screenHeightWithInsetsPx - min(contentHeightPx, maxSheetHeightPx)).coerceAtLeast(0f)
                                val targetCollapsedY = (screenHeightWithInsetsPx - min(newMeasuredHeight, maxSheetHeightPx)).coerceAtLeast(0f)
                                if (!dragging && !settling && !expanded && newMeasuredHeight > contentHeightPx + 1f) {
                                    contentHeightPx = newMeasuredHeight
                                    val wasNearPrevCollapsed = abs(offsetY.value - prevCollapsedY) < 3f ||
                                            abs(offsetY.value - collapsedY) < 3f
                                    if (wasNearPrevCollapsed) {
                                        scope.launch {
                                            offsetY.animateTo(
                                                targetValue = targetCollapsedY,
                                                animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                                            )
                                        }
                                    }
                                } else if (!dragging && !settling && !expanded && newMeasuredHeight < contentHeightPx - 1f) {
                                    contentHeightPx = newMeasuredHeight
                                    val wasNearPrevCollapsed = abs(offsetY.value - prevCollapsedY) < 3f ||
                                            abs(offsetY.value - collapsedY) < 3f
                                    if (wasNearPrevCollapsed) {
                                        scope.launch {
                                            offsetY.animateTo(
                                                targetValue = targetCollapsedY,
                                                animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                                            )
                                        }
                                    }
                                } else if (contentHeightPx <= 0f && newMeasuredHeight > 0f) {
                                    contentHeightPx = newMeasuredHeight
                                }
                            }
                            .pointerInput(visible, collapsedHeightPx, collapsedY, expanded) {
                                detectVerticalDragGestures(
                                    onDragStart = { dragging = true },
                                    onVerticalDrag = { _, dragAmount ->
                                        scope.launch {
                                            val new = (offsetY.value + dragAmount)
                                                .coerceIn(0f, screenHeightWithInsetsPx)
                                            offsetY.snapTo(new)
                                        }
                                    },
                                    onDragCancel = {
                                        dragging = false
                                        scope.launch {
                                            val y = offsetY.value
                                            val closeThreshold = collapsedY + (collapsedHeightPx * closeThresholdFractionOfContent)

                                            when {
                                                y >= closeThreshold -> {
                                                    settling = true
                                                    expanded = false
                                                    offsetY.animateTo(
                                                        targetValue = screenHeightWithInsetsPx,
                                                        animationSpec = tween(220)
                                                    )
                                                    settling = false
                                                    onDismissRequest()
                                                }
                                                y <= screenHeightWithInsetsPx * expandToTopTriggerFractionOfScreen -> {
                                                    expanded = true
                                                    offsetY.animateTo(
                                                        targetValue = 0f,
                                                        animationSpec = spring(stiffness = Spring.StiffnessLow)
                                                    )
                                                }
                                                expanded && y <= screenHeightWithInsetsPx * collapseFromTopTriggerFractionOfScreen -> {
                                                    offsetY.animateTo(
                                                        targetValue = 0f,
                                                        animationSpec = spring(stiffness = Spring.StiffnessLow)
                                                    )
                                                }
                                                else -> {
                                                    settling = true
                                                    expanded = false
                                                    offsetY.animateTo(
                                                        targetValue = collapsedY,
                                                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                                                    )
                                                    settling = false
                                                }
                                            }
                                        }
                                    },
                                    onDragEnd = {
                                        dragging = false
                                        scope.launch {
                                            val y = offsetY.value
                                            val closeThreshold = collapsedY + (collapsedHeightPx * closeThresholdFractionOfContent)

                                            when {
                                                y >= closeThreshold -> {
                                                    settling = true
                                                    expanded = false
                                                    offsetY.animateTo(
                                                        targetValue = screenHeightWithInsetsPx,
                                                        animationSpec = tween(220)
                                                    )
                                                    settling = false
                                                    onDismissRequest()
                                                }
                                                y <= screenHeightWithInsetsPx * expandToTopTriggerFractionOfScreen -> {
                                                    expanded = true
                                                    offsetY.animateTo(
                                                        targetValue = 0f,
                                                        animationSpec = spring(stiffness = Spring.StiffnessLow)
                                                    )
                                                }
                                                expanded && y <= screenHeightWithInsetsPx * collapseFromTopTriggerFractionOfScreen -> {
                                                    offsetY.animateTo(
                                                        targetValue = 0f,
                                                        animationSpec = spring(stiffness = Spring.StiffnessLow)
                                                    )
                                                }
                                                else -> {
                                                    settling = true
                                                    expanded = false
                                                    offsetY.animateTo(
                                                        targetValue = collapsedY,
                                                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                                                    )
                                                    settling = false
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                            .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 8.dp, bottom = 12.dp)
                                    .align(Alignment.CenterHorizontally)
                                    .clip(RoundedCornerShape(50))
                                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                                    .padding(vertical = 2.dp, horizontal = 20.dp)
                            )
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                            Box(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                                content()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun TodoistBottomSheetPreview() {
    var visible by remember { mutableStateOf(true) }
    val user1 = User(id = "1", email = "john@example.com", displayName = "John Doe", avatarUrl = null)
    val user2 = User(id = "2", email = "alice@example.com", displayName = "Alice", avatarUrl = null)
    val task = Task(
        id = "1",
        projectId = "proj1",
        owner = user1,
        title = "Implement Authentication",
        description = "Set up Firebase authentication with email and password. Ensure secure login and registration flows. " +
                "\n\nAlso, integrate social logins if possible.",
        createdAt = Timestamp.now(),
        deadline = Timestamp.now(),
        status = Status.IN_PROGRESS,
        priority = Priority.HIGH,
        updatedAt = null,
        tags = listOf("Android", "Firebase", "Backend", "Spring", "JWT"),
        assignedTo = listOf(
            user1,
            user2
        )
    )

    TODOTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Box(modifier = Modifier.fillMaxSize()) {
                TodoistBottomSheet(
                    visible = visible,
                    onDismissRequest = { visible = false },
                ) {
                    TaskDetailsScreen(task = task, {}, {})
                }
            }
        }
    }
}