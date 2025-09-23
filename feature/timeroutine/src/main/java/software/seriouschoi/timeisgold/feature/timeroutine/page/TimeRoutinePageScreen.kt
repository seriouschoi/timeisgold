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
    val density = LocalDensity.current
    val hourHeight = 60.dp
    val hourHeightPx = density.run { hourHeight.toPx() }
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
        var dragOffset by remember { mutableFloatStateOf(0f) }
        state.slotItemList.forEach { slot ->
            val startMinutes = slot.startTime.asMinutes()
            val endMinutes = slot.endTime.asMinutes()
            val topOffset = (startMinutes / 60f) * hourHeight
            val slotHeight = ((endMinutes - startMinutes) / 60f) * hourHeight

            TimeSlot(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(slotHeight)
                    .padding(start = 40.dp)
                    .offset(y = topOffset)
                    .draggable(
                        orientation = Orientation.Vertical,
                        state = rememberDraggableState {
                            dragOffset += it
                        },
                        onDragStopped = { velocity ->
                            // 드래그 끝 → 새로운 시간 계산
                            val movedMinutes = (dragOffset / hourHeightPx) * 60
                            val newStart = slot.startTime.plusMinutes(movedMinutes.roundToLong())
                            val newEnd = slot.endTime.plusMinutes(movedMinutes.roundToLong())

                            sendIntent(
                                TimeRoutinePageUiIntent.UpdateSlot(
                                    slot.uuid,
                                    newStart,
                                    newEnd
                                )
                            )
                            dragOffset = 0f
                        }
                    ),
                slotItem = slot,
            ) {
                sendIntent(it)
            }
        }
    }
}


@Composable
private fun TimeSlot(
    modifier: Modifier = Modifier,
    slotItem: TimeRoutinePageSlotItemUiState,
    sendIntent: (TimeRoutinePageUiIntent) -> Unit
) {
    Card(
        modifier = modifier
            .padding(10.dp),
        onClick = {
            sendIntent(slotItem.slotClickIntent)
        }
    ) {
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

@TigThemePreview
@Composable
private fun TimeSlotCardPreview() {
    TigTheme {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            TimeSlot(
                slotItem = TimeRoutinePageSlotItemUiState(
                    title = "타이틀",
                    startTime = LocalTime.now(),
                    endTime = LocalTime.now(),
                    slotClickIntent = TimeRoutinePageUiIntent.CreateRoutine,
                    uuid = "uuid"
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                //no work.
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
                        startTime = LocalTime.of(0, 30),
                        endTime = LocalTime.of(3, 0),
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
