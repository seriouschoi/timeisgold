package software.seriouschoi.timeisgold.feature.timeroutine.page

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import software.seriouschoi.timeisgold.core.common.ui.TigTheme
import software.seriouschoi.timeisgold.core.common.ui.TigThemePreview
import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.core.common.ui.asString
import software.seriouschoi.timeisgold.core.common.ui.components.TigLabelButton
import software.seriouschoi.timeisgold.core.common.ui.times.TimePixelUtil
import software.seriouschoi.timeisgold.core.common.util.Envelope
import software.seriouschoi.timeisgold.core.common.util.asMinutes
import timber.log.Timber
import java.time.DayOfWeek
import java.time.LocalTime
import kotlin.math.absoluteValue

/**
 * Created by jhchoi on 2025. 8. 26.
 * jhchoi
 */
@Composable
fun TimeRoutinePageScreen(
    dayOfWeek: DayOfWeek,
) {
    val viewModel = hiltViewModel<TimeRoutinePageViewModel>(key = dayOfWeek.name)
    val uiState by viewModel.uiState.collectAsState()
    val uiEvent by viewModel.uiEvent.collectAsState(null)
    val dragRefreshEvent by rememberUpdatedState(uiEvent?.payload as? TimeRoutinePageUiEvent.TimeSlotDragCursorRefresh)

    LaunchedEffect(dayOfWeek) {
        viewModel.load(dayOfWeek)
    }

    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        content = { innerPadding ->
            Box(
                Modifier.Companion
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                StateView(uiState, dragRefreshEvent) {
                    viewModel.sendIntent(it)
                }
                EventView(uiEvent, snackBarHostState)
            }
        },
        bottomBar = {

        }
    )


}

@Composable
private fun EventView(
    event: Envelope<TimeRoutinePageUiEvent>?,
    snackBar: SnackbarHostState
) {
    val context = LocalContext.current
    LaunchedEffect(event) {
        when (val payload = event?.payload) {
            is TimeRoutinePageUiEvent.ShowToast -> {

                val message = payload.message.asString(context)
                snackBar.showSnackbar(message)
            }

            is TimeRoutinePageUiEvent.TimeSlotDragCursorRefresh,
            null -> {
                //no work.
            }
        }
    }
}

@Composable
private fun StateView(
    currentState: TimeRoutinePageUiState,
    dragRefreshEvent: TimeRoutinePageUiEvent.TimeSlotDragCursorRefresh?,
    sendIntent: (TimeRoutinePageUiIntent) -> Unit
) {
    when (currentState) {
        is TimeRoutinePageUiState.Loading -> {
            Loading(currentState)
        }

        is TimeRoutinePageUiState.Routine -> {
            Routine(currentState, dragRefreshEvent) {
                sendIntent.invoke(it)
            }
        }

        is TimeRoutinePageUiState.Error -> {
            Error(currentState) {
                sendIntent.invoke(it)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Routine(
    state: TimeRoutinePageUiState.Routine,
    dragRefreshEvent: TimeRoutinePageUiEvent.TimeSlotDragCursorRefresh?,
    sendIntent: (TimeRoutinePageUiIntent) -> Unit,
) {
    val currentSlotList by rememberUpdatedState(state.slotItemList)
    val dragCursorRefreshEvent by rememberUpdatedState(dragRefreshEvent)
    val hourHeight = 60.dp
    val density = LocalDensity.current
    val hourHeightPx = density.run { hourHeight.toPx() }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
    ) {
        TimeSliceView(
            hourHeight = hourHeight,
            modifier = Modifier.fillMaxWidth()
        )

        val slotBoundsMap = remember { mutableStateMapOf<Int, Rect>() }

        val gesture1 = Modifier.verticalDragGesture(
            key = Unit,
            itemList = { currentSlotList },
            slotBoundsMap = {slotBoundsMap},
            onSelected = { item, target ->
                //no work.
            },
            onDrag = { item, dx, dy, target ->
                val minutesFactor = TimePixelUtil.pxToMinutes(dy.toLong(), hourHeightPx)
                val newStartTime = item.startMinutesOfDay + minutesFactor.toInt()
                val newEndTime = item.endMinutesOfDay + minutesFactor.toInt()
                val updateTime = when (target) {
                    DragTarget.Card -> Pair(newStartTime, newEndTime)
                    DragTarget.Top -> Pair(
                        newStartTime,
                        item.endMinutesOfDay
                    )

                    DragTarget.Bottom -> Pair(
                        item.startMinutesOfDay,
                        newEndTime
                    )
                }
                val intent = TimeRoutinePageUiIntent.UpdateTimeSlotUi(
                    uuid = item.slotUuid,
                    newStart = updateTime.first,
                    newEnd = updateTime.second,
                    orderChange = target == DragTarget.Card
                )
                sendIntent.invoke(intent)
            },
            onDrop = { item, target ->
                val intent = TimeRoutinePageUiIntent.UpdateTimeSlotList
                sendIntent.invoke(intent)
            },
            onTap = { item, target ->
                val intent = TimeRoutinePageUiIntent.ShowSlotEdit(
                    slotId = item.slotUuid,
                    routineId = item.routineUuid
                )
                sendIntent.invoke(intent)
            }
        )

        // TODO: jhchoi 2025. 10. 2. 여기 분리하기.
        val gesture = Modifier.pointerInput(Unit) {
            awaitEachGesture {
                val down = awaitFirstDown()
                Timber.d("down position. x=${down.position.x}, y=${down.position.y}")

                val hit = slotBoundsMap.entries.firstOrNull {
                    it.value.contains(down.position)
                }
                if (hit == null) return@awaitEachGesture
                Timber.d("gesture hit. index=${hit.key}")

                val selectedSlot = currentSlotList.getOrNull(hit.key) ?: return@awaitEachGesture

                val activeDragTarget = when {
                    down.position.y - hit.value.top < 20.dp.toPx() -> DragTarget.Top
                    down.position.y - hit.value.top > hit.value.height - 20.dp.toPx() -> DragTarget.Bottom
                    else -> DragTarget.Card
                }

                var distanceXAbs = 0L
                var distanceYAbs = 0L
                var distanceY = 0L
                val movedOffset = Offset(5f, 5f)
                var isMoved = false
                var longPressed = false

                val downTimeStamp = System.currentTimeMillis()
                var cursorSlot: TimeSlotItemUiState = selectedSlot
                while (true) {
                    val event = awaitPointerEvent().changes.firstOrNull() ?: break

                    val refreshCursorSlot = dragCursorRefreshEvent?.cursorSlotItem
                    if (refreshCursorSlot != null && refreshCursorSlot != cursorSlot) {
                        distanceY = 0
                        cursorSlot = refreshCursorSlot
                    }

                    val dragAmount = event.positionChange()
                    distanceXAbs += dragAmount.x.absoluteValue.toLong()
                    distanceYAbs += dragAmount.y.absoluteValue.toLong()
                    distanceY += dragAmount.y.toLong()

                    val minutesFactor = TimePixelUtil.pxToMinutes(distanceY, hourHeightPx)
                    val newStartTime = cursorSlot.startMinutesOfDay + minutesFactor.toInt()
                    val newEndTime = cursorSlot.endMinutesOfDay + minutesFactor.toInt()
                    val updateTime = when (activeDragTarget) {
                        DragTarget.Card -> Pair(newStartTime, newEndTime)
                        DragTarget.Top -> Pair(
                            newStartTime,
                            cursorSlot.endMinutesOfDay
                        )

                        DragTarget.Bottom -> Pair(
                            cursorSlot.startMinutesOfDay,
                            newEndTime
                        )
                    }

                    if (event.pressed.not()) {
                        Timber.d("up!")
                        if (longPressed) {
                            val intent = TimeRoutinePageUiIntent.UpdateTimeSlotList
                            sendIntent.invoke(intent)
                            return@awaitEachGesture
                        }

                        if (!isMoved) {
                            val intent = TimeRoutinePageUiIntent.ShowSlotEdit(
                                slotId = cursorSlot.slotUuid,
                                routineId = cursorSlot.routineUuid
                            )
                            sendIntent.invoke(intent)
                            return@awaitEachGesture
                        }
                        event.consume()
                        break
                    }

                    if (longPressed) {
                        event.consume()

                        val dragIntent = TimeRoutinePageUiIntent.UpdateTimeSlotUi(
                            uuid = cursorSlot.slotUuid,
                            newStart = updateTime.first,
                            newEnd = updateTime.second,
                            orderChange = false
                        ).let {
                            when (activeDragTarget) {
                                DragTarget.Card -> it.copy(
                                    orderChange = true
                                )

                                DragTarget.Top,
                                DragTarget.Bottom -> it
                            }
                        }

                        sendIntent.invoke(dragIntent)

                        continue
                    }

                    if (distanceXAbs > movedOffset.x || distanceYAbs > movedOffset.y) {
                        if (!isMoved) {
                            Timber.d("move!! distanceX=$distanceXAbs, distanceY=$distanceYAbs")
                        }
                        isMoved = true
                    }

                    val now = System.currentTimeMillis()
                    if (cursorSlot.isSelected) {
                        longPressed = true
                        continue
                    }
                    if (now - downTimeStamp > 200) {
                        if (!isMoved) {
                            //long press!!
                            Timber.d("long press!!")
                            longPressed = true
                        }
                        continue
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(hourHeight * 24)
                .then(gesture1)
        ) {
            state.slotItemList.forEachIndexed { index, slot ->
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
fun TimeSliceView(hourHeight: Dp, modifier: Modifier) {
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


@TigThemePreview
@Composable
private fun PreviewRoutine() {
    TigTheme {
        val startTime = LocalTime.of(1, 30)
        val endTime = LocalTime.of(4, 20)
        Routine(
            TimeRoutinePageUiState.Routine(
                title = "루틴 1",
                dayOfWeekName = "월",
                slotItemList = listOf(
                    TimeSlotItemUiState(
                        slotUuid = "temp_uuid",
                        routineUuid = "temp_routine_uuid",
                        title = "Some Slot Title",
                        startMinutesOfDay = startTime.asMinutes(),
                        endMinutesOfDay = endTime.asMinutes(),
                        slotClickIntent = TimeRoutinePageUiIntent.UpdateTimeSlotUi(
                            uuid = "temp_uuid",
                            newStart = startTime.asMinutes(),
                            newEnd = endTime.asMinutes(),
                            orderChange = false
                        ),
                        isSelected = false,
                    )
                ),
                dayOfWeeks = listOf(
                    DayOfWeek.MONDAY,
                )
            ),
            null
        ) {

        }
    }
}

@Composable
private fun Error(
    currentState: TimeRoutinePageUiState.Error,
    sendIntent: (TimeRoutinePageUiIntent) -> Unit,
) {
    val confirmButtonState by remember {
        mutableStateOf(currentState.confirmButton)
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = currentState.errorMessage.asString())
        val buttonState = confirmButtonState
        if (buttonState != null) {
            TigLabelButton(
                buttonState.buttonLabel.asString()
            ) {
                sendIntent(buttonState.intent)
            }
        }
    }
}

@Composable
@Preview
private fun PreviewError() {
    Error(
        TimeRoutinePageUiState.Error(
            errorMessage = UiText.Raw("알 수 없는 오류.")
        )
    ) {

    }
}


@Composable
private fun Loading(state: TimeRoutinePageUiState.Loading) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = state.loadingMessage.asString())
    }
}

@Preview
@Composable
private fun PreviewLoading() {
    Loading(
        TimeRoutinePageUiState.Loading(
            UiText.Raw("불러오는 중...")
        )
    )
}

private fun <T> Modifier.verticalDragGesture(
    key: Any,
    itemList: () -> List<T>,
    slotBoundsMap: () -> Map<Int, Rect>,
    onSelected: (item: T, dragTarget: DragTarget) -> Unit,
    onDrag: (item: T, dx: Float, dy: Float, dragTarget: DragTarget) -> Unit,
    onDrop: (item: T, dragTarget: DragTarget) -> Unit,
    onTap: (item: T, dragTarget: DragTarget) -> Unit
) = pointerInput(key) {
    awaitEachGesture {
        val down = awaitFirstDown()
        Timber.d("down position. x=${down.position.x}, y=${down.position.y}")

        val hit = slotBoundsMap().entries.firstOrNull {
            it.value.contains(down.position)
        }
        if (hit == null) return@awaitEachGesture
        val activeDragTarget = when {
            down.position.y - hit.value.top < 20.dp.toPx() -> DragTarget.Top
            down.position.y - hit.value.top > hit.value.height - 20.dp.toPx() -> DragTarget.Bottom
            else -> DragTarget.Card
        }
        val selectedItem = itemList().getOrNull(hit.key) ?: return@awaitEachGesture
        onSelected(selectedItem, activeDragTarget)
        Timber.d("gesture hit. index=${hit.key}, activeDragTarget=${activeDragTarget}")

        var distanceX = 0f
        var distanceY = 0f
        var distanceXAbs = 0f
        var distanceYAbs = 0f
        val downTimeStamp = System.currentTimeMillis()
        val movedOffset = Offset(5f, 5f)
        var isMoved = false
        var longPressed = false
        while (true) {
            val event = awaitPointerEvent().changes.firstOrNull() ?: break
            if (event.pressed.not()) {
                event.consume()
                if (longPressed) {
                    onDrop(selectedItem, activeDragTarget)
                    break
                }
                if (!isMoved) {
                    onTap(selectedItem, activeDragTarget)
                    break
                }
                break
            }

            val dragAmount = event.positionChange()
            distanceX += dragAmount.x
            distanceY += dragAmount.y
            distanceXAbs += dragAmount.x.absoluteValue
            distanceYAbs += dragAmount.y.absoluteValue

            if (longPressed) {
                event.consume()

                onDrag(selectedItem, distanceX, distanceY, activeDragTarget)
                continue
            }

            if (distanceXAbs > movedOffset.x || distanceYAbs > movedOffset.y) {
                if (!isMoved) {
                    Timber.d("move!! distanceX=$distanceXAbs, distanceY=$distanceYAbs")
                }
                isMoved = true
            }

            val now = System.currentTimeMillis()
            if (now - downTimeStamp > 200) {
                if (!isMoved) {
                    //long press!!
                    Timber.d("long press!!")
                    longPressed = true
                }
            }
        }
    }
}

private enum class DragTarget { Card, Top, Bottom }