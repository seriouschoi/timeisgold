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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
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
    val density = LocalDensity.current
    val hourHeightPx = density.run { hourHeight.toPx() }

    var dragOffset by remember { mutableFloatStateOf(0f) }
    var topHandleDragOffset by remember { mutableFloatStateOf(0f) }
    var bottomHandleDragOffset by remember { mutableFloatStateOf(0f) }

    val startMinutes = slotItem.startTime.asMinutes()
    val endMinutes = slotItem.endTime.asMinutes()
    val topOffset = (startMinutes / 60f) * hourHeight

//    위 핸들을 위로 올리면, offset은 음수가 되고, 사이즈는 늘어나야 한다.
//    아래 핸들을 위로 올리면, offset은 음수가 되고, 사이즈는 줄어야 한다.
    val topHandleHeightFactor = topHandleDragOffset.let { density.run { it.toDp() * -1 } }
    val bottomHandleHeightFactor = bottomHandleDragOffset.let { density.run { it.toDp() } }
    val slotHeightFactor = topHandleHeightFactor + bottomHandleHeightFactor
    val slotHeight = (((endMinutes - startMinutes) / 60f) * hourHeight) + slotHeightFactor

    //drag.
    val dragModifier = modifier
        .offset {
            val newY =
                topOffset.roundToPx() + dragOffset.roundToInt() + topHandleDragOffset.roundToInt()
            IntOffset(
                x = 0,
                y = newY
            )
        }
        .draggable(
            orientation = Orientation.Vertical,
            state = rememberDraggableState {
                dragOffset += it
            },
            onDragStopped = { velocity ->
                // 드래그 끝 → 새로운 시간 계산
                val movedMinutes = (dragOffset / hourHeightPx) * 60
                val newStart = slotItem.startTime.plusMinutes(movedMinutes.roundToLong())
                val newEnd = slotItem.endTime.plusMinutes(movedMinutes.roundToLong())
                dragOffset = 0f

                sendIntent(
                    TimeRoutinePageUiIntent.UpdateSlot(
                        slotItem.uuid,
                        newStart,
                        newEnd
                    )
                )
            }
        )

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
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(8.dp)
                    .draggable(
                        orientation = Orientation.Vertical,
                        state = rememberDraggableState {
                            topHandleDragOffset += it
                        },
                        onDragStopped = { velocity ->
                            // 드래그 끝 → 새로운 시간 계산
                            val movedMinutes = (topHandleDragOffset / hourHeightPx) * 60
                            val newStart =
                                slotItem.startTime.plusMinutes(movedMinutes.roundToLong())
                            val newEnd = slotItem.endTime
                            topHandleDragOffset = 0f

                            sendIntent(
                                TimeRoutinePageUiIntent.UpdateSlot(
                                    slotItem.uuid,
                                    newStart,
                                    newEnd
                                )
                            )
                        }
                    )
            )

            //bottom handle.
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(8.dp)
                    .draggable(
                        orientation = Orientation.Vertical,
                        state = rememberDraggableState {
                            bottomHandleDragOffset += it
                        },
                        onDragStopped = { velocity ->
                            // 드래그 끝 → 새로운 시간 계산
                            val movedMinutes = (bottomHandleDragOffset / hourHeightPx) * 60
                            val newStart = slotItem.startTime
                            val newEnd = slotItem.endTime.plusMinutes(movedMinutes.roundToLong())
                            bottomHandleDragOffset = 0f

                            sendIntent(
                                TimeRoutinePageUiIntent.UpdateSlot(
                                    slotItem.uuid,
                                    newStart,
                                    newEnd
                                )
                            )
                        }
                    )
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
                        text = slotItem.startTime.asFormattedString(),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = slotItem.endTime.asFormattedString(),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

    }
}