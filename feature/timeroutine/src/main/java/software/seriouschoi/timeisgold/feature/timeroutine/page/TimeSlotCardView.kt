package software.seriouschoi.timeisgold.feature.timeroutine.page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import software.seriouschoi.timeisgold.core.common.util.asFormattedString
import software.seriouschoi.timeisgold.core.common.util.asMinutes
import java.time.LocalTime
import kotlin.math.round
import kotlin.math.roundToInt

@Composable
internal fun TimeSlotCardView(
    modifier: Modifier = Modifier,
    slotItem: TimeSlotCardUiState,
    hourHeight: Dp,
    sendIntent: (TimeRoutinePageUiIntent) -> Unit,
) {
    var startTime by remember { mutableStateOf(slotItem.startTime) }
    var endTime by remember { mutableStateOf(slotItem.endTime) }

    val density = LocalDensity.current
    val hourHeightPx = density.run { hourHeight.toPx() }

    val startMinutes = startTime.asMinutes()
    val endMinutes = endTime.asMinutes()
    val topOffsetPx = startMinutes.minutesToPx(hourHeightPx)

    var activeDragTarget by remember { mutableStateOf<DragTarget?>(null) }

    fun commitUpdate() {
        activeDragTarget = null
        startTime = startTime.normalize()
        endTime = endTime.normalize()
        sendIntent(
            TimeRoutinePageUiIntent.UpdateSlot(
                slotItem.uuid,
                startTime,
                endTime
            )
        )
    }

    // height.
    val slotHeight = (endMinutes - startMinutes).minutesToPx(hourHeightPx).let {
        density.run { it.toDp() }
    }
    val slotHeightPx = slotHeight.let { density.run { it.toPx() } }

    //drag.
    val cardLongPressDrag = Modifier.pointerInput(Unit) {
        detectDragGesturesAfterLongPress(
            onDragStart = {
                activeDragTarget = when {
                    it.y < (20.dp.toPx()) -> {
                        DragTarget.Top
                    }
                    it.y > (slotHeightPx - 20.dp.toPx()) -> {
                        DragTarget.Bottom
                    }
                    else -> {
                        DragTarget.Card
                    }
                }
            },
            onDragEnd = {
                commitUpdate()
            },
            onDragCancel = { activeDragTarget = null },
            onDrag = { change: PointerInputChange, dragAmount: Offset ->
                change.consume() //부모 스크롤 전파 차단.
                when(activeDragTarget) {
                    DragTarget.Card -> {
                        val minutesFactor = dragAmount.y.pxToMinutes(hourHeightPx).toLong()
                        startTime = startTime.plusMinutes(minutesFactor)
                        endTime = endTime.plusMinutes(minutesFactor)
                    }
                    DragTarget.Top -> {
                        val minutesFactor = dragAmount.y.pxToMinutes(hourHeightPx).toLong()
                        startTime = startTime.plusMinutes(minutesFactor)
                    }
                    DragTarget.Bottom -> {
                        val minutesFactor = dragAmount.y.pxToMinutes(hourHeightPx).toLong()
                        endTime = endTime.plusMinutes(minutesFactor)
                    }
                    null -> {
                        //no work
                    }
                }
            },
        )
    }

    val cardColor = if(activeDragTarget != null) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainer
    }

    Card(
        modifier = modifier
            .offset {
                IntOffset(
                    x = 0,
                    y = topOffsetPx.roundToInt()
                )
            }
            .height(slotHeight)
            .padding(start = 40.dp)
            .then(cardLongPressDrag),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        )
    ) {
        Box(Modifier.fillMaxSize()) {

            //content
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = slotItem.title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.clickable {
                        sendIntent(slotItem.slotClickIntent)
                    })
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
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

private fun LocalTime.normalize(minute: Float = 15f): LocalTime {
    val newMinute = (round(this.minute / minute) * minute).toInt() % 60
    return this.withMinute(newMinute)
}

private fun Float.pxToMinutes(hourHeightPx: Float): Float {
    return (this / hourHeightPx) * 60
}

private fun Int.minutesToPx(hourHeightPx: Float): Float {
    return (this / 60f) * hourHeightPx
}

private enum class DragTarget { Card, Top, Bottom }