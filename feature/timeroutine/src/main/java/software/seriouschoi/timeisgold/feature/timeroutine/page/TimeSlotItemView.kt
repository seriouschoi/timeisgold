package software.seriouschoi.timeisgold.feature.timeroutine.page

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputEventHandler
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import software.seriouschoi.timeisgold.core.common.ui.TigTheme
import software.seriouschoi.timeisgold.core.common.util.LocalDateTimeUtil
import software.seriouschoi.timeisgold.core.common.util.asFormattedString
import software.seriouschoi.timeisgold.core.common.util.asMinutes
import software.seriouschoi.timeisgold.core.common.util.normalize
import timber.log.Timber
import java.time.LocalTime
import kotlin.math.absoluteValue

@Deprecated("new time slot item view.")
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
                endTime,
                false
            )
        )
    }
    if (slotItem.startTime > slotItem.endTime) {
        TimeDraggableCardView(
            modifier = modifier,
            hourHeight = hourHeight,
            slotItem = slotItem,
            onClick = {
                sendIntent(slotItem.slotClickIntent)
            },
            startMinutes = slotItem.startTime.asMinutes(),
            endMinutes = LocalDateTimeUtil.DAY_MINUTES + slotItem.endTime.asMinutes(),
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
            startMinutes = 0 - (LocalDateTimeUtil.DAY_MINUTES - slotItem.startTime.asMinutes()),
            endMinutes = slotItem.endTime.asMinutes(),
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
            startMinutes = slotItem.startTime.asMinutes(),
            endMinutes = slotItem.endTime.asMinutes(),
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
    startMinutes: Int,
    endMinutes: Int,
    slotItem: TimeSlotCardUiState,
    hourHeight: Dp,
    onClick: () -> Unit,
    onDragStop: (LocalTime, LocalTime) -> Unit
) {
    var draggableStartMinutes by remember {
        mutableIntStateOf(startMinutes)
    }
    var draggableEndMinutes by remember {
        mutableIntStateOf(endMinutes)
    }
    LaunchedEffect(startMinutes, endMinutes) {
        draggableStartMinutes = startMinutes
        draggableEndMinutes = endMinutes
    }
    Timber.d("show card. title=${slotItem.title}, draggableStartMinutes=${draggableStartMinutes}, draggableEndMinutes=${draggableEndMinutes}, startMinutes=$startMinutes, endMinutes=$endMinutes")

    val density = LocalDensity.current
    val hourHeightPx = density.run { hourHeight.toPx() }

    val topOffsetPx = draggableStartMinutes.minutesToPx(hourHeightPx)

    // height.
    val slotHeight = (draggableEndMinutes - draggableStartMinutes).minutesToPx(hourHeightPx).let {
        density.run { it.toDp() }
    }
    val slotHeightPx = slotHeight.let { density.run { it.toPx() } }

    var longPressed by remember { mutableStateOf(false) }
    var isDowned by remember { mutableStateOf(false) }


    val pointerInputEventHandler: PointerInputEventHandler = remember {
        PointerInputEventHandler {
            Timber.d("PointerInput block CREATED")
            val movedOffset = Offset(5f, 5f)
            awaitEachGesture {
                val down = awaitFirstDown()
                if (isDowned) {
                    Timber.d("downed.")
                    return@awaitEachGesture
                }

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
                Timber.d("down. activeDragTarget=$activeDragTarget, down.position.y=${down.position.y}, bottomPosition=${slotHeightPx - 20.dp.toPx()}")

                val downTimeStamp = System.currentTimeMillis()

                while (true) {
                    val event = awaitPointerEvent().changes.firstOrNull() ?: break
                    if (event.pressed.not()) {
                        Timber.d("up. distanceX=$distanceX, distanceY=$distanceY")
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
                    if (now - downTimeStamp > 300) {
                        if (!isMoved && !longPressed) {
                            //long press!!
                            Timber.d("long press!!")
                            longPressed = true
                        }
                    }

                    if (longPressed) {
                        event.consume()

                        when (activeDragTarget) {
                            DragTarget.Card -> {
                                val minutesFactor = dragAmount.y.pxToMinutes(hourHeightPx).toInt()

                                draggableStartMinutes += minutesFactor
                                draggableEndMinutes += minutesFactor
                            }

                            DragTarget.Top -> {
                                val minutesFactor = dragAmount.y.pxToMinutes(hourHeightPx).toInt()
                                draggableStartMinutes += minutesFactor
                            }

                            DragTarget.Bottom -> {
                                val minutesFactor = dragAmount.y.pxToMinutes(hourHeightPx).toInt()
                                draggableEndMinutes += minutesFactor
                            }
                        }
                    }
                }

                Timber.d("gesture finished. longPressed=$longPressed, isMoved=$isMoved")
                isDowned = false
                if (longPressed) {
                    onDragStop(
                        draggableStartMinutes.minutesToLocalTime().normalize(),
                        draggableEndMinutes.minutesToLocalTime().normalize()
                    )
                } else {
                    if (!isMoved) {
                        onClick()
                    }
                }
                longPressed = false
            }
        }
    }
    val cardGestureModifier = remember { Modifier.pointerInput(Unit, pointerInputEventHandler) }

    ItemCardView(
        modifier = modifier.then(cardGestureModifier),
        isLongPressed = longPressed,
        title = slotItem.title,
        startTime = draggableStartMinutes.minutesToLocalTime().asFormattedString(),
        endTime = draggableEndMinutes.minutesToLocalTime().asFormattedString(),
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
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainer
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
                Text(
                    text = startTime,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = endTime,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    TigTheme {
        ItemCardView(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            title = "title",
            startTime = "09:00",
            endTime = "10:00",
            heightDp = 50.dp,
            topOffsetPx = 0,
            isLongPressed = false
        )
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