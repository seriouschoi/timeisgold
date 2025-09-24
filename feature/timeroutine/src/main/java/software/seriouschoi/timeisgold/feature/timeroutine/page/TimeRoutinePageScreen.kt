package software.seriouschoi.timeisgold.feature.timeroutine.page

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import software.seriouschoi.timeisgold.core.common.ui.TigTheme
import software.seriouschoi.timeisgold.core.common.ui.TigThemePreview
import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.core.common.ui.asString
import software.seriouschoi.timeisgold.core.common.ui.components.TigLabelButton
import timber.log.Timber
import java.time.DayOfWeek
import java.time.LocalTime
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
        Timber.d("slotItemList size=${state.slotItemList.size}")
        state.slotItemList.forEach { slot ->
            TimeSlotItemView(
                modifier = Modifier.fillMaxWidth(),
                slotItem = slot,
                hourHeight = hourHeight
            ) {
                sendIntent(it)
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
                    TimeSlotCardUiState(
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
