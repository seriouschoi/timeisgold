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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import software.seriouschoi.timeisgold.core.common.ui.TigTheme
import software.seriouschoi.timeisgold.core.common.ui.TigThemePreview
import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.core.common.ui.asString
import software.seriouschoi.timeisgold.core.common.ui.components.TigLabelButton
import software.seriouschoi.timeisgold.core.common.util.asFormattedString
import software.seriouschoi.timeisgold.core.common.util.asMinutes
import java.time.DayOfWeek
import java.time.LocalTime
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import software.seriouschoi.timeisgold.core.common.ui.R as CommonR

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

    val uiState by remember {
        viewModel.uiState
    }.collectAsState(
        initial = TimeRoutinePageUiState.Loading(
            loadingMessage = UiText.MultipleResArgs.create(
                CommonR.string.message_format_loading,
                CommonR.string.text_routine
            )
        )
    )

    val currentState = uiState

    when (currentState) {
        is TimeRoutinePageUiState.Loading -> {
            Loading(currentState)
        }

        is TimeRoutinePageUiState.Routine -> {
            Routine(currentState) {
                viewModel.sendIntent(it)
            }
        }

        is TimeRoutinePageUiState.Error -> {
            Error(currentState) {
                viewModel.sendIntent(it)
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
    val hourHeight = 60.dp
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(hourHeight * 24)
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
        state.slotItemList.forEach { slot ->
            TimeSlotCardView(
                modifier = Modifier.fillMaxWidth(),
                slotItem = slot,
                hourHeight = hourHeight
            ) {
                sendIntent(it)
            }
        }
    }
}


@Composable
private fun TimeSlotCardView(
    modifier: Modifier = Modifier,
    slotItem: TimeRoutinePageSlotItemUiState,
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
    val bottomHandleHeightFactor = bottomHandleDragOffset.let { density.run { it.toDp()}}
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

@TigThemePreview
@Composable
private fun PreviewRoutine() {
    TigTheme {
        Routine(
            TimeRoutinePageUiState.Routine(
                title = "루틴 1",
                dayOfWeekName = "월",
                slotItemList = listOf(
                    TimeRoutinePageSlotItemUiState(
                        title = "타이틀",
                        startTime = LocalTime.of(4, 30),
                        endTime = LocalTime.of(6, 0),
                        slotClickIntent = TimeRoutinePageUiIntent.CreateRoutine,
                        uuid = "uuid"
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
    sendIntent: (TimeRoutinePageUiIntent) -> Unit
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
