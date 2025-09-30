package software.seriouschoi.timeisgold.feature.timeroutine.page

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import software.seriouschoi.timeisgold.core.common.util.asFormattedString
import software.seriouschoi.timeisgold.core.common.util.asMinutes
import java.time.DayOfWeek
import java.time.LocalTime

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

    // TODO: jhchoi 2025. 9. 30. scaffolds 추가..
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
    val hourHeight = 60.dp
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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(hourHeight * 24)
        ) {
            state.slotItemList.forEach { slot ->
                TimeSlotItemView(
                    modifier = Modifier.fillMaxWidth(),
                    slotItem = slot,
                    hourHeight = hourHeight,
                    sendIntent = sendIntent
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
                            onlyUi = false
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
