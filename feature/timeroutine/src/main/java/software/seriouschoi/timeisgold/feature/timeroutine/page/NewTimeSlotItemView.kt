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
import software.seriouschoi.timeisgold.core.common.util.LocalDateTimeUtil
import software.seriouschoi.timeisgold.core.common.util.asFormattedString
import software.seriouschoi.timeisgold.core.common.util.normalize
import timber.log.Timber
import kotlin.math.absoluteValue

@Composable
internal fun NewTimeSlotItemView(
    modifier: Modifier = Modifier,
    slotItem: NewTimeSlotCardUiState,
    hourHeight: Dp,
    sendIntent: (TimeRoutinePageUiIntent) -> Unit,
) {

    val currentSlot by rememberUpdatedState(slotItem)
    Timber.d("slotItem changed: $currentSlot")
    val density = LocalDensity.current
    val hourHeightPx = density.run { hourHeight.toPx() }

    val topOffsetPx = currentSlot.startMinutesOfDay.minutesToPx(hourHeightPx)

    // height.
    val slotHeightPx =
        (currentSlot.endMinutesOfDay - currentSlot.startMinutesOfDay).minutesToPx(hourHeightPx)
    val slotHeight = slotHeightPx.let { density.run { it.toDp() } }
    Timber.d(
        """
        show card. 
        title=${currentSlot.title}, 
        startMinutesOfDay=${LocalDateTimeUtil.createFromMinutesOfDay(currentSlot.startMinutesOfDay.toLong())}, 
        endMinutesOfDay=${LocalDateTimeUtil.createFromMinutesOfDay(currentSlot.endMinutesOfDay.toLong())},
    """.trimIndent()
    )

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
                    down.position.y < 20.dp.toPx() -> NewDragTarget.Top
                    down.position.y > (slotHeightPx - 20.dp.toPx()) -> NewDragTarget.Bottom
                    else -> NewDragTarget.Card
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

                        // TODO: 어..왠지 캡쳐 문제 같은데..여기 스레드가..이전의 것을 가지고 있는것 같아. 그걸 기반으로 더할려니..안되는것 같고..
                        when (activeDragTarget) {
                            NewDragTarget.Card -> {
                                val minutesFactor = dragAmount.y.pxToMinutes(hourHeightPx).toInt()
                                Timber.d(
                                    "drag. item. startTime=${
                                        LocalDateTimeUtil.createFromMinutesOfDay(
                                            currentSlot.startMinutesOfDay.toLong()
                                        )
                                    }, endTime=${LocalDateTimeUtil.createFromMinutesOfDay(currentSlot.startMinutesOfDay.toLong())}"
                                )
                                val startTime =
                                    LocalDateTimeUtil.createFromMinutesOfDay(currentSlot.startMinutesOfDay.toLong() + minutesFactor)
                                val endTime =
                                    LocalDateTimeUtil.createFromMinutesOfDay(currentSlot.endMinutesOfDay.toLong() + minutesFactor)
                                sendIntent.invoke(
                                    TimeRoutinePageUiIntent.UpdateSlot(
                                        currentSlot.slotUuid,
                                        startTime,
                                        endTime,
                                        true
                                    )
                                )
                            }

                            NewDragTarget.Top -> {
                                val minutesFactor = dragAmount.y.pxToMinutes(hourHeightPx).toInt()
                                val startTime =
                                    LocalDateTimeUtil.createFromMinutesOfDay(currentSlot.startMinutesOfDay.toLong() + minutesFactor)
                                sendIntent.invoke(
                                    TimeRoutinePageUiIntent.UpdateSlot(
                                        currentSlot.slotUuid,
                                        startTime,
                                        LocalDateTimeUtil.createFromMinutesOfDay(currentSlot.endMinutesOfDay.toLong()),
                                        true
                                    )
                                )
                            }

                            NewDragTarget.Bottom -> {
                                val minutesFactor = dragAmount.y.pxToMinutes(hourHeightPx).toInt()
                                val endTime =
                                    LocalDateTimeUtil.createFromMinutesOfDay(currentSlot.endMinutesOfDay.toLong() + minutesFactor)
                                sendIntent.invoke(
                                    TimeRoutinePageUiIntent.UpdateSlot(
                                        uuid = currentSlot.slotUuid,
                                        newStart = LocalDateTimeUtil.createFromMinutesOfDay(currentSlot.startMinutesOfDay.toLong()),
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
                        LocalDateTimeUtil.createFromMinutesOfDay(currentSlot.startMinutesOfDay.toLong())
                            .normalize()
                    val endTime =
                        LocalDateTimeUtil.createFromMinutesOfDay(currentSlot.endMinutesOfDay.toLong())
                            .normalize()
                    sendIntent.invoke(
                        TimeRoutinePageUiIntent.UpdateSlot(
                            uuid = currentSlot.slotUuid,
                            newStart = startTime,
                            newEnd = endTime,
                            onlyState = false
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
                longPressed = false
            }
        }
    }
    val cardGestureModifier = remember { Modifier.pointerInput(Unit, pointerInputEventHandler) }

    ItemCardView(
        modifier = modifier.then(cardGestureModifier),
        title = currentSlot.title,
        startTime = LocalDateTimeUtil.createFromMinutesOfDay(currentSlot.startMinutesOfDay.toLong())
            .asFormattedString(),
        endTime = LocalDateTimeUtil.createFromMinutesOfDay(currentSlot.endMinutesOfDay.toLong())
            .asFormattedString(),
        heightDp = slotHeight,
        topOffsetPx = topOffsetPx.toInt(),
        isLongPressed = longPressed,
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
    isLongPressed: Boolean,
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


private fun Float.pxToMinutes(hourHeightPx: Float): Float {
    return (this / hourHeightPx) * 60
}

private fun Int.minutesToPx(hourHeightPx: Float): Float {
    return (this / 60f) * hourHeightPx
}

private enum class NewDragTarget { Card, Top, Bottom }