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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
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
import software.seriouschoi.timeisgold.core.common.util.LocalTimeUtil
import software.seriouschoi.timeisgold.core.common.util.normalize
import timber.log.Timber
import kotlin.math.absoluteValue

@Composable
internal fun TimeSlotItemView(
    modifier: Modifier = Modifier,
    slotItem: TimeSlotCardUiState,
    hourHeight: Dp,
    sendIntent: (TimeRoutinePageUiIntent) -> Unit,
) {
    val currentSlot by rememberUpdatedState(slotItem)
    val density = LocalDensity.current
    val hourHeightPx = density.run { hourHeight.toPx() }

    Timber.d(
        """
        show card. 
        title=${currentSlot.title}, 
        startMinutesOfDay=${LocalTimeUtil.create(currentSlot.startMinutesOfDay.toLong())}, 
        endMinutesOfDay=${LocalTimeUtil.create(currentSlot.endMinutesOfDay.toLong())},
    """.trimIndent()
    )

    var isDowned by remember { mutableStateOf(false) }

    val cardGestureModifier = remember { Modifier.pointerInput(Unit) {
        Timber.d("PointerInput block CREATED")
        val movedOffset = Offset(5f, 5f)
        awaitEachGesture {
            val down = awaitFirstDown()
            var longPressed = false
            if (isDowned) {
                Timber.d("downed.")
                return@awaitEachGesture
            }
            // TODO: jhchoi 2025. 9. 29. 분리 해야함.
            isDowned = true
            var distanceX = 0L
            var distanceY = 0L
            var isMoved = false
            val slotHeightPx =
                (currentSlot.endMinutesOfDay - currentSlot.startMinutesOfDay).minutesToPx(
                    hourHeightPx
                )
            val activeDragTarget = when {
                down.position.y < 20.dp.toPx() -> DragTarget.Top
                down.position.y > (slotHeightPx - 20.dp.toPx()) -> DragTarget.Bottom
                else -> DragTarget.Card
            }
            Timber.d("down. activeDragTarget=$activeDragTarget, slotHeightPx=$slotHeightPx, down.position.y=${down.position.y}")

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
                            val startTime =
                                LocalTimeUtil.create(currentSlot.startMinutesOfDay.toLong() + minutesFactor)
                            val endTime =
                                LocalTimeUtil.create(currentSlot.endMinutesOfDay.toLong() + minutesFactor)
                            sendIntent.invoke(
                                TimeRoutinePageUiIntent.UpdateSlot(
                                    uuid = currentSlot.slotUuid,
                                    newStart = startTime,
                                    newEnd = endTime,
                                    onlyUi = true,

                                    )
                            )
                        }

                        DragTarget.Top -> {
                            val minutesFactor = dragAmount.y.pxToMinutes(hourHeightPx).toInt()
                            val startTime =
                                LocalTimeUtil.create(currentSlot.startMinutesOfDay.toLong() + minutesFactor)
                            sendIntent.invoke(
                                TimeRoutinePageUiIntent.UpdateSlot(
                                    currentSlot.slotUuid,
                                    startTime,
                                    LocalTimeUtil.create(currentSlot.endMinutesOfDay.toLong()),
                                    true
                                )
                            )
                        }

                        DragTarget.Bottom -> {
                            val minutesFactor = dragAmount.y.pxToMinutes(hourHeightPx).toInt()
                            val endTime =
                                LocalTimeUtil.create(currentSlot.endMinutesOfDay.toLong() + minutesFactor)
                            sendIntent.invoke(
                                TimeRoutinePageUiIntent.UpdateSlot(
                                    uuid = currentSlot.slotUuid,
                                    newStart = LocalTimeUtil.create(
                                        currentSlot.startMinutesOfDay.toLong()
                                    ),
                                    newEnd = endTime,
                                    true
                                )
                            )
                        }
                    }
                }
            }

            Timber.d("gesture finished. longPressed=$longPressed, isMoved=$isMoved")
            isDowned = false
            if (longPressed) {
                val startTime =
                    LocalTimeUtil.create(currentSlot.startMinutesOfDay.toLong())
                        .normalize()
                val endTime =
                    LocalTimeUtil.create(currentSlot.endMinutesOfDay.toLong())
                        .normalize()
                sendIntent.invoke(
                    TimeRoutinePageUiIntent.UpdateSlot(
                        uuid = currentSlot.slotUuid,
                        newStart = startTime,
                        newEnd = endTime,
                        onlyUi = false
                    )
                )
            } else {
                if (!isMoved) {
                    sendIntent.invoke(
                        TimeRoutinePageUiIntent.ShowSlotEdit(
                            slotId = currentSlot.slotUuid,
                            routineId = currentSlot.routineUuid

                        )
                    )
                }
            }
        }
    }
    }

    val topOffsetPx = currentSlot.startMinutesOfDay.minutesToPx(hourHeightPx)
    val slotHeightPx =
        (currentSlot.endMinutesOfDay - currentSlot.startMinutesOfDay).minutesToPx(hourHeightPx)
    ItemCardView(
        modifier = modifier.then(cardGestureModifier),
        item = currentSlot,
        heightDp = slotHeightPx.let { density.run { it.toDp() } },
        topOffsetPx = topOffsetPx.toInt(),
    )
}

@Composable
private fun ItemCardView(
    modifier: Modifier,
    item: TimeSlotCardUiState,
    heightDp: Dp,
    topOffsetPx: Int,
) {
    val cardColor = if (item.isSelected) {
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
                    text = item.title,
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = item.startMinuteText,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = item.endMinuteText,
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
                .fillMaxWidth(),
            item = TimeSlotCardUiState(
                slotUuid = "slotUuid",
                routineUuid = "routineUuid",
                title = "title",
                startMinutesOfDay = 0,
                endMinutesOfDay = 60,
                startMinuteText = "09:00",
                endMinuteText = "10:00",
                slotClickIntent = TimeRoutinePageUiIntent.ShowSlotEdit(
                    slotId = "slotUuid",
                    routineId = "routineUuid"
                ),
                isSelected = false
            ),
            heightDp = 50.dp,
            topOffsetPx = 0,
        )
    }
}


private fun Float.pxToMinutes(hourHeightPx: Float): Float {
    return (this / hourHeightPx) * 60
}

private fun Int.minutesToPx(hourHeightPx: Float): Float {
    return (this / 60f) * hourHeightPx
}

private enum class DragTarget { Card, Top, Bottom }