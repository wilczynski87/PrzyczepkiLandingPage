package com.example.przyczepki_landingpage.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.przyczepki_landingpage.GateOpenUiState
import kotlin.math.roundToInt

@Composable
fun GateOpenSlider(
    gateState: GateOpenUiState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onOpenRequested: () -> Unit = {},
) {
    val density = LocalDensity.current
    val thumbSize = 48.dp
    val trackHeight = 56.dp
    val thumbSizePx = with(density) { thumbSize.toPx() }
    val horizontalPaddingPx = with(density) { 4.dp.toPx() }

    var trackWidthPx by remember { mutableStateOf(0f) }
    var dragOffsetPx by remember { mutableStateOf(0f) }

    val maxDragPx = (trackWidthPx - thumbSizePx - horizontalPaddingPx * 2).coerceAtLeast(0f)
    val isInteractive = enabled && gateState is GateOpenUiState.Idle

    LaunchedEffect(gateState, maxDragPx) {
        dragOffsetPx = when (gateState) {
            GateOpenUiState.Idle,
            is GateOpenUiState.Error,
            -> 0f

            GateOpenUiState.Loading,
            GateOpenUiState.Success,
            -> maxDragPx
        }
    }

    val animatedOffsetPx by animateFloatAsState(targetValue = dragOffsetPx)

    val trackColor = when (gateState) {
        GateOpenUiState.Success -> MaterialTheme.colorScheme.primaryContainer
        is GateOpenUiState.Error -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val label = when (gateState) {
        GateOpenUiState.Idle -> "Przesuń, aby otworzyć bramę"
        GateOpenUiState.Loading -> "Oczekiwanie na odpowiedź..."
        GateOpenUiState.Success -> "Brama otwarta"
        is GateOpenUiState.Error -> "Nie udało się otworzyć bramy"
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(trackHeight)
                .clip(RoundedCornerShape(percent = 50))
                .background(trackColor)
                .onSizeChanged { trackWidthPx = it.width.toFloat() },
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = label,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = thumbSize + 8.dp),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            animatedOffsetPx.roundToInt() + horizontalPaddingPx.roundToInt(),
                            0,
                        )
                    }
                    .padding(vertical = 4.dp)
                    .size(thumbSize)
                    .clip(CircleShape)
                    .background(
                        if (isInteractive || gateState is GateOpenUiState.Loading) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        },
                    )
                    .pointerInput(isInteractive, maxDragPx) {
                        if (!isInteractive) return@pointerInput
                        var openedThisDrag = false
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                if (dragOffsetPx < maxDragPx * 0.85f) {
                                    dragOffsetPx = 0f
                                }
                            },
                            onHorizontalDrag = { _, dragAmount ->
                                dragOffsetPx = (dragOffsetPx + dragAmount).coerceIn(0f, maxDragPx)
                                if (dragOffsetPx >= maxDragPx * 0.85f && !openedThisDrag) {
                                    dragOffsetPx = maxDragPx
                                    openedThisDrag = true
                                    onOpenRequested()
                                }
                            },
                        )
                    },
                contentAlignment = Alignment.Center,
            ) {
                when (gateState) {
                    GateOpenUiState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }

                    else -> {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                }
            }
        }

        if (gateState is GateOpenUiState.Error) {
            Text(
                text = gateState.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}
