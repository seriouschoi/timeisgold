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
import software.seriouschoi.timeisgold.core.common.util.Envelope
import software.seriouschoi.timeisgold.core.common.util.LocalTimeUtil
import software.seriouschoi.timeisgold.core.common.util.asFormattedString
import software.seriouschoi.timeisgold.core.common.util.asMinutes
import software.seriouschoi.timeisgold.core.common.util.normalize
import timber.log.Timber
import java.time.DayOfWeek
import java.time.LocalTime
import kotlin.math.abs
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
                val uiState by viewModel.uiState.collectAsState()
                StateView(uiState) {
                    viewModel.sendIntent(it)
                }
                val uiEvent by viewModel.uiEvent.collectAsState(null)
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

            null -> {
                //no work.
            }
        }
    }
}

@Composable
private fun StateView(
    currentState: TimeRoutinePageUiState,
    sendIntent: (TimeRoutinePageUiIntent) -> Unit
) {
    when (currentState) {
        is TimeRoutinePageUiState.Loading -> {
            Loading(currentState)
        }

        is TimeRoutinePageUiState.Routine -> {
            Routine(currentState) {
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
    sendIntent: (TimeRoutinePageUiIntent) -> Unit,
) {
    val currentSlotList by rememberUpdatedState(state.slotItemList)
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

        val gesture = Modifier.pointerInput(Unit) {
            awaitEachGesture {
                val down = awaitFirstDown()
                Timber.d("down position. x=${down.position.x}, y=${down.position.y}")

                val hit = slotBoundsMap.entries.firstOrNull {
                    it.value.contains(down.position)
                }
                if (hit == null) return@awaitEachGesture
                Timber.d("gesture hit. index=${hit.key}")

                val activeDragTarget = when {
                    down.position.y - hit.value.top < 20.dp.toPx() -> RoutinePageSlotItemDragTarget.Top
                    down.position.y - hit.value.top > hit.value.height - 20.dp.toPx() -> RoutinePageSlotItemDragTarget.Bottom
                    else -> RoutinePageSlotItemDragTarget.Card
                }

                // TODO: jhchoi 2025. 10. 1. swap을 해도... 이걸 계속 들고 있으니깐... 이걸로 기준으로 계속 갱신 하는구나..
                /*
                차라리 연속 스왑을 제공하지 말자.
                 */
                val currentSlot = currentSlotList.getOrNull(hit.key)
                if (currentSlot == null) {
                    Timber.d("not found slot item.")
                    return@awaitEachGesture
                }

                var distanceXAbs = 0L
                var distanceYAbs = 0L
                var distanceY = 0L
                val movedOffset = Offset(5f, 5f)
                var isMoved = false
                var longPressed = false

                val downTimeStamp = System.currentTimeMillis()
                while (true) {

                    val event = awaitPointerEvent().changes.firstOrNull() ?: break

                    val dragAmount = event.positionChange()
                    distanceXAbs += dragAmount.x.absoluteValue.toLong()
                    distanceYAbs += dragAmount.y.absoluteValue.toLong()
                    distanceY += dragAmount.y.toLong()

                    val minutesFactor = distanceY.toFloat().pxToMinutes(hourHeightPx).toLong()
                    val newStartTime =
                        LocalTimeUtil.create(currentSlot.startMinutesOfDay + minutesFactor)
                    val newEndTime =
                        LocalTimeUtil.create(currentSlot.endMinutesOfDay + minutesFactor)
                    val updateTime = when (activeDragTarget) {
                        RoutinePageSlotItemDragTarget.Card -> Pair(newStartTime, newEndTime)
                        RoutinePageSlotItemDragTarget.Top -> Pair(
                            newStartTime,
                            LocalTimeUtil.create(currentSlot.endMinutesOfDay.toLong())
                        )

                        RoutinePageSlotItemDragTarget.Bottom -> Pair(
                            LocalTimeUtil.create(currentSlot.startMinutesOfDay.toLong()),
                            newEndTime
                        )
                    }

                    if (event.pressed.not()) {
                        Timber.d("up!")
                        if (longPressed) {
                            val intent = TimeRoutinePageUiIntent.UpdateSlot(
                                uuid = currentSlot.slotUuid,
                                newStart = updateTime.first.normalize(),
                                newEnd = updateTime.second.normalize(),
                                onlyUi = false,
                                orderChange = false
                            )
                            sendIntent.invoke(intent)
                            return@awaitEachGesture
                        }

                        if (!isMoved) {
                            val intent = TimeRoutinePageUiIntent.ShowSlotEdit(
                                slotId = currentSlot.slotUuid,
                                routineId = currentSlot.routineUuid
                            )
                            sendIntent.invoke(intent)
                            return@awaitEachGesture
                        }
                        event.consume()
                        break
                    }

                    if (longPressed) {
                        event.consume()

                        val dragIntent = TimeRoutinePageUiIntent.UpdateSlot(
                            uuid = currentSlot.slotUuid,
                            newStart = updateTime.first,
                            newEnd = updateTime.second,
                            onlyUi = true,
                            orderChange = false
                        ).let {
                            when(activeDragTarget){
                                RoutinePageSlotItemDragTarget.Card -> it.copy(
                                    orderChange = true
                                )
                                RoutinePageSlotItemDragTarget.Top,
                                RoutinePageSlotItemDragTarget.Bottom -> it
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
                    if (currentSlot.isSelected) {
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
                .then(gesture)
        ) {
            state.slotItemList.forEachIndexed { index, slot ->
                val topOffsetPx = slot.startMinutesOfDay.minutesToPx(hourHeightPx)
                val slotHeightPx =
                    abs(slot.endMinutesOfDay - slot.startMinutesOfDay).minutesToPx(hourHeightPx)

                val globalPositioned = Modifier.onGloballyPositioned {
                    val bounds = it.boundsInParent()
                    slotBoundsMap[index] = bounds
                }
                TimeSlotItemCardView(
                    modifier = Modifier.fillMaxWidth(),
                    item = slot,
                    heightDp = density.run { slotHeightPx.toDp() },
                    topOffsetPx = topOffsetPx.toInt(),
                    globalPositioned = globalPositioned,
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
                    TimeSlotCardUiState(
                        slotUuid = "temp_uuid",
                        routineUuid = "temp_routine_uuid",
                        title = "Some Slot Title",
                        startMinutesOfDay = startTime.asMinutes(),
                        endMinutesOfDay = endTime.asMinutes(),
                        startMinuteText = startTime.asFormattedString(),
                        endMinuteText = endTime.asFormattedString(),
                        slotClickIntent = TimeRoutinePageUiIntent.UpdateSlot(
                            uuid = "temp_uuid",
                            newStart = startTime,
                            newEnd = endTime,
                            onlyUi = false,
                            orderChange = false
                        ),
                        isSelected = false,
                    )
                ),
                dayOfWeeks = listOf(
                    DayOfWeek.MONDAY,
                )
            )
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

private fun Float.pxToMinutes(hourHeightPx: Float): Float {
    return (this / hourHeightPx) * 60
}

private fun Int.minutesToPx(hourHeightPx: Float): Float {
    return (this / 60f) * hourHeightPx
}

private enum class RoutinePageSlotItemDragTarget { Card, Top, Bottom }