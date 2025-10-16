package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import software.seriouschoi.timeisgold.core.common.ui.components.DragTarget
import software.seriouschoi.timeisgold.core.common.ui.components.multipleGesture
import software.seriouschoi.timeisgold.core.common.ui.times.TimePixelUtil
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.TimeRoutinePageUiIntent
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.TimeSlotListPageUiState
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.TimeSliceView
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.TimeSlotUpdateTimeType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TimeSlotListView(
    state: TimeSlotListPageUiState,
    modifier: Modifier = Modifier,
    sendIntent: (TimeRoutinePageUiIntent) -> Unit,
) {
    val currentSlotList by rememberUpdatedState(state.slotItemList)
    val hourHeight = 60.dp
    val density = LocalDensity.current
    val hourHeightPx = density.run { hourHeight.toPx() }

    Box(
        modifier = modifier.verticalScroll(
            rememberScrollState()
        )
    ) {
        TimeSliceView(
            hourHeight = hourHeight,
            modifier = Modifier.Companion.fillMaxWidth()
        )

        val slotBoundsMap = remember { mutableStateMapOf<Int, Rect>() }

        val gesture1 = Modifier.Companion.multipleGesture(
            key = Unit,
            slotBoundsMap = { slotBoundsMap },
            onSelected = { index, target ->
                //no work.
            },
            onDrag = onDrag@{ index, dx, dy, target ->
                val selectedItemUuid = currentSlotList.getOrNull(index)?.slotUuid ?: return@onDrag
                val minutesFactor = TimePixelUtil.pxToMinutes(dy.toLong(), hourHeightPx)
                val updateTime = when (target) {
                    DragTarget.Card -> TimeSlotUpdateTimeType.START_AND_END
                    DragTarget.Top -> TimeSlotUpdateTimeType.START
                    DragTarget.Bottom -> TimeSlotUpdateTimeType.END
                }
                val intent = TimeRoutinePageUiIntent.UpdateTimeSlotUi(
                    uuid = selectedItemUuid,
                    minuteFactor = minutesFactor.toInt(),
                    updateTimeType = updateTime
                )
                sendIntent.invoke(intent)
            },
            onDrop = { index, target ->
                val intent = TimeRoutinePageUiIntent.UpdateTimeSlotList
                sendIntent.invoke(intent)
            },
            onTap = onTab@{ index, target ->
                val selectedItem = currentSlotList.getOrNull(index) ?: return@onTab
                val intent = TimeRoutinePageUiIntent.ShowSlotEdit(
                    slotId = selectedItem.slotUuid,
                    routineId = selectedItem.routineUuid
                )
                sendIntent.invoke(intent)
            }
        )

        Box(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .height(hourHeight * 24)
                .then(gesture1)
        ) {
            state.slotItemList.forEachIndexed { index, slot ->
                val globalPositioned = Modifier.Companion.onGloballyPositioned {
                    val bounds = it.boundsInParent()
                    slotBoundsMap[index] = bounds
                }
                TimeSlotItemCardView(
                    modifier = Modifier.Companion.fillMaxWidth(),
                    item = slot,
                    globalPositioned = globalPositioned,
                    hourHeight = hourHeight
                )
            }
        }
    }
}