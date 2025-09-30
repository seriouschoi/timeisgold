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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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
    Timber.d("TimeSlotItemView CREATED/UPDATED. slotItem.uuid=${slotItem.slotUuid}")
    val currentSlot by rememberUpdatedState(slotItem)
    val density = LocalDensity.current
    val hourHeightPx = density.run { hourHeight.toPx() }

    val cardGestureModifier = Modifier.pointerInput(currentSlot.slotUuid) {
        Timber.d("PointerInput block CREATED")
        val movedOffset = Offset(5f, 5f)
        awaitEachGesture {
            val down = awaitFirstDown()
            Timber.d("down=${down}")
            var longPressed = false
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
            Timber.d("activeDragTarget=$activeDragTarget")

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
                if (now - downTimeStamp > 200) {
                    if (!isMoved && !longPressed) {
                        //long press!!
                        Timber.d("long press!!")
                        longPressed = true
                    }
                }

                if (longPressed) {
                    event.consume()
                    val minutesFactor = dragAmount.y.pxToMinutes(hourHeightPx).toInt()
                    val newStartTime =
                        LocalTimeUtil.create(currentSlot.startMinutesOfDay.toLong() + minutesFactor)
                    val newEndTime =
                        LocalTimeUtil.create(currentSlot.endMinutesOfDay.toLong() + minutesFactor)
                    val intent = when (activeDragTarget) {
                        DragTarget.Card -> TimeRoutinePageUiIntent.UpdateSlot(
                            uuid = currentSlot.slotUuid,
                            newStart = newStartTime,
                            newEnd = newEndTime,
                            onlyUi = true,
                        )

                        DragTarget.Top -> TimeRoutinePageUiIntent.UpdateSlot(
                            uuid = currentSlot.slotUuid,
                            newStart = newStartTime,
                            newEnd = LocalTimeUtil.create(currentSlot.endMinutesOfDay.toLong()),
                            onlyUi = true
                        )

                        DragTarget.Bottom -> TimeRoutinePageUiIntent.UpdateSlot(
                            uuid = currentSlot.slotUuid,
                            newStart = LocalTimeUtil.create(
                                currentSlot.startMinutesOfDay.toLong()
                            ),
                            newEnd = newEndTime,
                            onlyUi = true
                        )
                    }
                    sendIntent.invoke(intent)
                }
            }

            Timber.d("gesture finished. longPressed=$longPressed, isMoved=$isMoved")
            when {
                longPressed -> {
                    val startTime =
                        LocalTimeUtil.create(currentSlot.startMinutesOfDay.toLong())
                            .normalize()
                    val endTime =
                        LocalTimeUtil.create(currentSlot.endMinutesOfDay.toLong())
                            .normalize()
                    TimeRoutinePageUiIntent.UpdateSlot(
                        uuid = currentSlot.slotUuid,
                        newStart = startTime,
                        newEnd = endTime,
                        onlyUi = false
                    ).let {
                        sendIntent.invoke(it)
                    }
                }

                !longPressed && !isMoved -> TimeRoutinePageUiIntent.ShowSlotEdit(
                    slotId = currentSlot.slotUuid,
                    routineId = currentSlot.routineUuid
                ).let {
                    sendIntent.invoke(it)
                }
            }
        }
    }

    val topOffsetPx = currentSlot.startMinutesOfDay.minutesToPx(hourHeightPx)
    val slotHeightPx =
        (currentSlot.endMinutesOfDay - currentSlot.startMinutesOfDay).minutesToPx(hourHeightPx)
    ItemCardView(
        modifier = modifier,
        item = currentSlot,
        heightDp = slotHeightPx.let { density.run { it.toDp() } },
        topOffsetPx = topOffsetPx.toInt(),
        gestureModifier = cardGestureModifier
    )
}

@Composable
private fun ItemCardView(
    modifier: Modifier,
    item: TimeSlotCardUiState,
    heightDp: Dp,
    topOffsetPx: Int,
    gestureModifier: Modifier = Modifier,
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
            .then(gestureModifier),
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
                isSelected = false,
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