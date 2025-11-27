package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import software.seriouschoi.timeisgold.core.common.ui.components.DragTarget
import software.seriouschoi.timeisgold.core.common.ui.components.multipleGesture
import software.seriouschoi.timeisgold.core.common.ui.times.TimePixelUtil
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.TimeSlotListPageUiIntent
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.item.TimeSlotItemCardView
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.item.TimeSlotItemUiState
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TimeSlotListView(
    slotItemList: List<TimeSlotItemUiState>,
    modifier: Modifier = Modifier,
    sendIntent: (TimeSlotListPageUiIntent) -> Unit,
) {
    val currentSlotList: List<TimeSlotItemUiState> by rememberUpdatedState(slotItemList)
    val hourHeight = 60.dp
    val density = LocalDensity.current
    val hourHeightPx = density.run { hourHeight.toPx() }

    Box(
        modifier = modifier.verticalScroll(
            rememberScrollState()
        )
    ) {
        val slotBoundsMap = remember { mutableStateMapOf<Int, Rect>() }

        val gesture1 = Modifier.multipleGesture(
            key = Unit,
            boundsMap = { slotBoundsMap },
            onSelectedBound = { index, target ->
                //no work.
            },
            onDragBound = onDrag@{ index, dx, dy, target ->
                val selectedItem = currentSlotList.getOrNull(index) ?: return@onDrag

                val minutesFactor = TimePixelUtil.pxToMinutes(dy.toLong(), hourHeightPx)
                val intent = when (target) {
                    DragTarget.Card -> {
                        TimeSlotListPageUiIntent.DragTimeSlotBody(
                            slotId = selectedItem.slotUuid,
                            minuteFactor = minutesFactor.toInt()
                        )
                    }
                    DragTarget.Top ->{
                        TimeSlotListPageUiIntent.DragTimeSlotHeader(
                            slotId = selectedItem.slotUuid,
                            minuteFactor = minutesFactor.toInt()
                        )

                    }
                    DragTarget.Bottom -> {
                        TimeSlotListPageUiIntent.DragTimeSlotFooter(
                            slotId = selectedItem.slotUuid,
                            minuteFactor = minutesFactor.toInt()
                        )
                    }
                }

                sendIntent.invoke(intent)
            },
            onDropBound = { index, target ->
                val intent = TimeSlotListPageUiIntent.ApplyTimeSlotListChanges
                sendIntent.invoke(intent)
            },
            onTapBound = onTab@{ index, target ->
                val selectedItem = currentSlotList.getOrNull(index) ?: return@onTab
                val intent = TimeSlotListPageUiIntent.SelectTimeSlot(
                    slot = selectedItem
                )
                sendIntent.invoke(intent)
            },
            onTapBoundElse = {

            }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(hourHeight * 24)
                .then(gesture1)
        ) {
            TimeSliceView(
                modifier = Modifier.fillMaxWidth(),
                hourHeight = hourHeight,
                sendIntent = sendIntent
            )

            slotItemList.forEachIndexed { index, slot ->
                val globalPositioned = Modifier.onGloballyPositioned {
                    val bounds = it.boundsInParent()
                    slotBoundsMap[index] = bounds
                }
                TimeSlotItemCardView(
                    modifier = Modifier.fillMaxWidth(),
                    item = slot,
                    globalPositioned = globalPositioned,
                    hourHeight = hourHeight
                )
            }
        }
    }
}

@Composable
internal fun TimeSliceView(
    hourHeight: Dp,
    modifier: Modifier,
    sendIntent: (TimeSlotListPageUiIntent) -> Unit
) {
    Column(
        modifier = modifier.height(hourHeight * 24)
    ) {
        repeat(24) { hour ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(hourHeight)
            ) {
                HorizontalDivider()

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(true) {
                            Timber.d("click time click. hour=$hour")
                            val intent = TimeSlotListPageUiIntent.SelectTimeSlice(
                                hourOfDay = hour
                            )
                            sendIntent.invoke(intent)
                        }
                ) {
                    Text(
                        text = "$hour:00",
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                HorizontalDivider()
            }
        }
    }
}