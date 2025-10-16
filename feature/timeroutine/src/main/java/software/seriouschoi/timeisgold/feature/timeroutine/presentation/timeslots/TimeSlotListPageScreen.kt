package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import software.seriouschoi.timeisgold.core.common.ui.TigTheme
import software.seriouschoi.timeisgold.core.common.ui.TigThemePreview
import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.core.common.ui.asString
import software.seriouschoi.timeisgold.core.common.ui.components.TigLabelButton
import software.seriouschoi.timeisgold.core.common.util.Envelope
import software.seriouschoi.timeisgold.core.common.util.asMinutes
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.TimeSlotItemUiState
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.TimeSlotListView
import java.time.DayOfWeek
import java.time.LocalTime
import software.seriouschoi.timeisgold.core.common.ui.R as CommonR

/**
 * Created by jhchoi on 2025. 8. 26.
 * jhchoi
 */
@Composable
fun TimeSlotListPageScreen(
    dayOfWeek: DayOfWeek,
) {
    val viewModel = hiltViewModel<TimeSlotListPageViewModel>(key = dayOfWeek.name)
    val uiState by viewModel.uiState.collectAsState()
    val uiEvent by viewModel.uiEvent.collectAsState(null)

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
                StateView(uiState) {
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
    event: Envelope<TimeSlotListPageUiEvent>?,
    snackBar: SnackbarHostState
) {
    val context = LocalContext.current
    LaunchedEffect(event) {
        when (val payload = event?.payload) {
            is TimeSlotListPageUiEvent.ShowToast -> {

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
    currentState: TimeSlotListPageUiState,
    sendIntent: (TimeRoutinePageUiIntent) -> Unit
) {
    when (currentState) {
        is TimeSlotListPageUiState.Data -> {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                TimeSlotListView(state = currentState, modifier = Modifier.fillMaxSize()) {
                    sendIntent.invoke(it)
                }
            }
            if (currentState.loadingMessage != null) {
                Loading(currentState.loadingMessage)
            }

            if (currentState.errorState != null) {
                Error(currentState.errorState) {
                    sendIntent.invoke(it)
                }
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
        TimeSlotListView(
            TimeSlotListPageUiState.Data(
                slotItemList = listOf(
                    TimeSlotItemUiState(
                        slotUuid = "temp_uuid",
                        routineUuid = "temp_routine_uuid",
                        title = "Some Slot Title",
                        startMinutesOfDay = startTime.asMinutes(),
                        endMinutesOfDay = endTime.asMinutes(),
                        isSelected = false,
                    )
                ),
            ),
        ) {

        }
    }
}

@Composable
private fun Error(
    errorState: TimeSlotListPageErrorState,
    sendIntent: (TimeRoutinePageUiIntent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.surfaceContainer
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = errorState.errorMessage.asString())
        val errorConfirmIntent = errorState.confirmIntent
        if (errorConfirmIntent != null) {
            TigLabelButton(
                stringResource(CommonR.string.text_confirm)
            ) {
                sendIntent(errorConfirmIntent)
            }
        }
    }
}

@Composable
@Preview
private fun PreviewError() {
    Error(
        TimeSlotListPageErrorState(
            UiText.Res.create(CommonR.string.message_format_routine_create_confirm, "월요일"),
            TimeRoutinePageUiIntent.CreateRoutine
        )
    ) {

    }
}


@Composable
private fun Loading(loadingMessage: UiText?) {
    if (loadingMessage != null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = loadingMessage.asString())
        }
    }
}

@Preview
@Composable
private fun PreviewLoading() {
    Loading(
        TimeSlotListPageUiState.Data().loadingState().loadingMessage
    )
}

