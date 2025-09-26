package software.seriouschoi.timeisgold.feature.timeroutine.page

import android.view.ViewConfiguration
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import software.seriouschoi.timeisgold.core.common.util.LocalDateTimeUtil
import software.seriouschoi.timeisgold.core.common.util.asFormattedString
import software.seriouschoi.timeisgold.core.common.util.asMinutes
import software.seriouschoi.timeisgold.core.common.util.normalize
import timber.log.Timber
import java.time.LocalTime
import kotlin.math.absoluteValue

@Composable
internal fun TimeSlotItemView(
    modifier: Modifier = Modifier,
    slotItem: TimeSlotCardUiState,
    hourHeight: Dp,
    sendIntent: (TimeRoutinePageUiIntent) -> Unit,
) {
    Timber.d("slotItem changed: $slotItem")
    fun updateTimeSlot(startTime: LocalTime, endTime: LocalTime) {
        Timber.d("dragStop. startTime=$startTime, endTime=$endTime")
        sendIntent(
            TimeRoutinePageUiIntent.UpdateSlot(
                slotItem.uuid,
                startTime,
                endTime
            )
        )
    }
    if (slotItem.startTime > slotItem.endTime) {
        TimeDraggableCardView(
            modifier = modifier,
            hourHeight = hourHeight,
            slotItem = slotItem,
            startTime = slotItem.startTime,
            onClick = {
                sendIntent(slotItem.slotClickIntent)
            },
            onDragStop = { startTime, endTime ->
                updateTimeSlot(
                    startTime, endTime
                )
            }
        )

        TimeDraggableCardView(
            modifier = modifier,
            hourHeight = hourHeight,
            slotItem = slotItem,
            endTime = slotItem.endTime,
            onClick = {
                sendIntent(slotItem.slotClickIntent)
            },
            onDragStop = { startTime, endTime ->
                updateTimeSlot(
                    startTime, endTime
                )
            }
        )
    } else {
        TimeDraggableCardView(
            modifier = modifier,
            hourHeight = hourHeight,
            slotItem = slotItem,
            startTime = slotItem.startTime,
            endTime = slotItem.endTime,
            onClick = {
                sendIntent(slotItem.slotClickIntent)
            },
            onDragStop = { startTime, endTime ->
                updateTimeSlot(
                    startTime, endTime
                )
            }
        )
    }
}

@Composable
private fun TimeDraggableCardView(
    modifier: Modifier,
    startTime: LocalTime? = null,
    endTime: LocalTime? = null,
    slotItem: TimeSlotCardUiState,
    hourHeight: Dp,
    onClick: () -> Unit,
    onDragStop: (LocalTime, LocalTime) -> Unit
) {
    var startMinutes by remember(slotItem) {
        mutableIntStateOf(startTime?.asMinutes() ?: 0)
    }
    var endMinutes by remember(slotItem) {
        mutableIntStateOf(endTime?.asMinutes()?.takeIf { it > 0 } ?: LocalDateTimeUtil.DAY_MINUTES)
    }

    val draggedStartTime: LocalTime by remember(slotItem) {
        derivedStateOf {
            //내부에서 변경하는 startMinutes에서 파생된 결과를 재구성 기준으로 써야하므로, derivedStateOf 사용.
            if (startTime == null) {
                slotItem.startTime.minusMinutes(
                    ((LocalDateTimeUtil.DAY_MINUTES - startMinutes) % LocalDateTimeUtil.DAY_MINUTES).toLong()
                )
            } else {
                startMinutes.minutesToLocalTime()
            }
        }
    }
    val draggedEndTime: LocalTime by remember(slotItem) {
        derivedStateOf {
            if (endTime == null) {
                slotItem.endTime.plusMinutes(endMinutes.toLong())
            } else {
                endMinutes.minutesToLocalTime()
            }
        }
    }

    Timber.d("show card. title=${slotItem.title}, startMinutes=$startMinutes, endMinutes=$endMinutes, draggedStartTime=$draggedStartTime, draggedEndTime=$draggedEndTime")

    val density = LocalDensity.current
    val hourHeightPx = density.run { hourHeight.toPx() }

    val topOffsetPx = startMinutes.minutesToPx(hourHeightPx)

    // height.
    val slotHeight = (endMinutes - startMinutes).minutesToPx(hourHeightPx).let {
        density.run { it.toDp() }
    }
    val slotHeightPx = slotHeight.let { density.run { it.toPx() } }

    var longPressed by remember { mutableStateOf(false) }

    fun commitUpdate() {
        longPressed = false
        onDragStop(
            draggedStartTime.normalize(),
            draggedEndTime.normalize()
        )
    }

    fun updateDragTime(activeDragTarget: DragTarget, dragAmount: Offset) {
        when (activeDragTarget) {
            DragTarget.Card -> {
                val minutesFactor = dragAmount.y.pxToMinutes(hourHeightPx).toInt()
                startMinutes += minutesFactor
                endMinutes += minutesFactor
            }

            DragTarget.Top -> {
                val minutesFactor = dragAmount.y.pxToMinutes(hourHeightPx).toInt()
                startMinutes += minutesFactor
            }

            DragTarget.Bottom -> {
                val minutesFactor = dragAmount.y.pxToMinutes(hourHeightPx).toInt()
                endMinutes += minutesFactor
            }
        }
    }

    val cardGestureModifier = Modifier.pointerInput(Triple(startTime, endTime, slotItem.uuid)) {
        val movedOffset = Offset(5f, 5f)
        var isDowned = false
        awaitEachGesture {
            val down = awaitFirstDown()
            if(isDowned) return@awaitEachGesture

            isDowned = true
            var distanceX = 0L
            var distanceY = 0L
            longPressed = false
            var isMoved = false
            val activeDragTarget = when {
                down.position.y < 20.dp.toPx() -> DragTarget.Top
                down.position.y > (slotHeightPx - 20.dp.toPx()) -> DragTarget.Bottom
                else -> DragTarget.Card
            }
            Timber.d("down. startTime=$startTime, endTime=$endTime")

            val downTimeStamp = System.currentTimeMillis()

            while (true) {
                val event = awaitPointerEvent().changes.firstOrNull() ?: break
                if (event.pressed.not()) {
                    Timber.d("up. distanceX=$distanceX, distanceY=$distanceY")
                    isDowned = false
                    event.consume()
                    break
                }

                val dragAmount = event.positionChange()
                distanceX += dragAmount.x.absoluteValue.toLong()
                distanceY += dragAmount.y.absoluteValue.toLong()
                if (distanceX > movedOffset.x || distanceY > movedOffset.y) {
                    if (!isMoved) {
                        Timber.d("move!! distanceX=$distanceX, distanceY=$distanceY")
                    }
                    isMoved = true
                }

                val now = System.currentTimeMillis()
                if (now - downTimeStamp > ViewConfiguration.getLongPressTimeout()) {
                    if (!isMoved) {
                        //long press!!
                        longPressed = true
                    }
                }

                if (longPressed) {
                    longPressed = true
                    event.consume()
                    updateDragTime(activeDragTarget, dragAmount)
                }
            }

            Timber.d("gesture finished. longPressed=$longPressed, isMoved=$isMoved")
            if (longPressed) {
                commitUpdate()
            } else {
                if (!isMoved) {
                    onClick()
                }
            }
            longPressed = false
        }
    }

    ItemCardView(
        modifier = modifier.then(cardGestureModifier),
        isLongPressed = longPressed,
        title = slotItem.title,
        startTime = draggedStartTime.asFormattedString(),
        endTime = draggedEndTime.asFormattedString(),
        heightDp = slotHeight,
        topOffsetPx = topOffsetPx.toInt()
    )
}

@Composable
private fun ItemCardView(
    modifier: Modifier,
    title: String,
    startTime: String,
    endTime: String,
    heightDp: Dp,
    topOffsetPx: Int,
    isLongPressed: Boolean
) {
    val cardColor = if (isLongPressed) {
        MaterialTheme.colorScheme.primaryContainer.copy(
            alpha = 0.5f
        )
    } else {
        MaterialTheme.colorScheme.surfaceContainer.copy(
            alpha = 0.5f
        )
    }
    Card(
        modifier = modifier
            .offset {
                IntOffset(
                    x = 0,
                    y = topOffsetPx
                )
            }
            .height(heightDp)
            .padding(start = 40.dp)
            .then(modifier),
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
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = startTime,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = endTime,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

private fun Int.minutesToLocalTime(): LocalTime {
    return LocalTime.of(0, 0).plusMinutes(this.toLong())
}

private fun Float.pxToMinutes(hourHeightPx: Float): Float {
    return (this / hourHeightPx) * 60
}

private fun Int.minutesToPx(hourHeightPx: Float): Float {
    return (this / 60f) * hourHeightPx
}

private enum class DragTarget { Card, Top, Bottom }