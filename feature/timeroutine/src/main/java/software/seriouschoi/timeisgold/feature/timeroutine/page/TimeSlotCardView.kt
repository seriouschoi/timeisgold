package software.seriouschoi.timeisgold.feature.timeroutine.page

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import software.seriouschoi.timeisgold.core.common.util.asFormattedString
import software.seriouschoi.timeisgold.core.common.util.asMinutes
import kotlin.math.roundToInt
import kotlin.math.roundToLong

@Composable
internal fun TimeSlotCardView(
    modifier: Modifier = Modifier,
    slotItem: TimeSlotCardUiState,
    hourHeight: Dp,
    sendIntent: (TimeRoutinePageUiIntent) -> Unit
) {
    var startTime by remember { mutableStateOf(slotItem.startTime) }
    var endTime by remember { mutableStateOf(slotItem.endTime) }

    val density = LocalDensity.current
    val hourHeightPx = density.run { hourHeight.toPx() }

    val startMinutes = startTime.asMinutes()
    val endMinutes = endTime.asMinutes()
    val topOffsetPx = startMinutes.minutesToPx(hourHeightPx)

    //drag.
    val dragModifier = modifier
        .offset {
            IntOffset(
                x = 0,
                y = topOffsetPx.roundToInt()
            )
        }
        .draggable(
            orientation = Orientation.Vertical,
            state = rememberDraggableState { it: Float ->
                val minutesFactor = it.pxToMinutes(hourHeightPx).toLong()
                startTime = startTime.plusMinutes(minutesFactor)
                endTime = endTime.plusMinutes(minutesFactor)
            },
            onDragStopped = { velocity ->
                sendIntent(
                    TimeRoutinePageUiIntent.UpdateSlot(
                        slotItem.uuid,
                        startTime,
                        endTime
                    )
                )
            }
        )

    val topHandleDragModifier = Modifier.draggable(
        orientation = Orientation.Vertical,
        state = rememberDraggableState {
            startTime = startTime.plusMinutes(it.pxToMinutes(hourHeightPx).roundToLong())
        },
        onDragStopped = { velocity ->
            sendIntent(
                TimeRoutinePageUiIntent.UpdateSlot(
                    slotItem.uuid,
                    startTime,
                    endTime
                )
            )
        }
    )

    val bottomHandleDragModifier = Modifier.draggable(
        orientation = Orientation.Vertical,
        state = rememberDraggableState {
            endTime = endTime.plusMinutes(it.pxToMinutes(hourHeightPx).roundToLong())
        },
        onDragStopped = { velocity ->
            sendIntent(
                TimeRoutinePageUiIntent.UpdateSlot(
                    slotItem.uuid,
                    startTime,
                    endTime
                )
            )
        }
    )

    // height.
    val slotHeight = (endMinutes - startMinutes).minutesToPx(hourHeightPx).let {
        density.run { it.toDp() }
    }

    Card(
        modifier = dragModifier
            .height(slotHeight)
            .padding(start = 40.dp),
        onClick = {
            sendIntent(slotItem.slotClickIntent)
        }
    ) {
        Box(Modifier.fillMaxSize()) {
            //top handle.
            Box(
                modifier = topHandleDragModifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(8.dp)
            )

            //bottom handle.
            Box(
                modifier = bottomHandleDragModifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(8.dp)
            )
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(text = slotItem.title, style = MaterialTheme.typography.titleLarge)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = startTime.asFormattedString(),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = endTime.asFormattedString(),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

    }
}

private fun Float.pxToMinutes(hourHeightPx: Float): Float {
    return (this / hourHeightPx) * 60
}

private fun Int.minutesToPx(hourHeightPx: Float): Float {
    return (this / 60f) * hourHeightPx
}