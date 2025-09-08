package software.seriouschoi.timeisgold.feature.timeroutine.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.core.common.ui.asString
import software.seriouschoi.timeisgold.core.common.ui.components.TigLabelButton
import software.seriouschoi.timeisgold.core.common.ui.components.TigText
import software.seriouschoi.timeisgold.feature.timeroutine.bar.R
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale
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
        TimeRoutinePageUiState.Loading(
            UiText.Res(CommonR.string.message_loading)
        )
    )

    val currentState = uiState

    when (currentState) {
        is TimeRoutinePageUiState.Loading -> {
            Loading(currentState)
        }

        is TimeRoutinePageUiState.Empty -> {
            Empty(currentState) {
                viewModel.sendIntent(it)
            }
        }

        is TimeRoutinePageUiState.Routine -> {
            Routine(currentState) {
                viewModel.sendIntent(it)
            }
        }

        is TimeRoutinePageUiState.Error -> {
            Error(currentState)
        }
    }
}

@Composable
private fun Routine(
    state: TimeRoutinePageUiState.Routine,
    sendIntent: (TimeRoutinePageUiIntent) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TigText(text = state.dayOfWeekName)
        TigText(text = state.title)
        TigLabelButton(
            label = stringResource(CommonR.string.text_edit),
            onClick = {
                sendIntent(TimeRoutinePageUiIntent.ModifyRoutine)
            }
        )
    }
}

@Composable
@Preview
private fun PreviewRoutine() {
    Routine(
        TimeRoutinePageUiState.Routine(
            title = "루틴 1",
            dayOfWeekName = "월요일",
            slotItemList = listOf(),
            dayOfWeeks = listOf()
        )
    ) {

    }
}

@Composable
private fun Error(currentState: TimeRoutinePageUiState.Error) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TigText(text = currentState.errorMessage.asString())
    }
}

@Composable
@Preview
private fun PreviewError() {
    Error(
        TimeRoutinePageUiState.Error(
            errorMessage = UiText.Res.create(
                CommonR.string.message_failed_load_data_by_unknown_error,
            )
        )
    )
}

@Composable
private fun Empty(
    currentState: TimeRoutinePageUiState.Empty,
    sendIntent: (TimeRoutinePageUiIntent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TigText(text = currentState.emptyMessage.asString())
        TigLabelButton(
            label = stringResource(CommonR.string.text_create),
            onClick = {
                sendIntent(TimeRoutinePageUiIntent.CreateRoutine)
            }
        )
    }
}

@Preview
@Composable
private fun PreviewEmpty() {
    Empty(
        currentState = TimeRoutinePageUiState.Empty(
            emptyMessage = UiText.Res.create(
                R.string.message_routine_create_confirm, DayOfWeek.MONDAY.getDisplayName(
                    TextStyle.FULL, Locale.getDefault()
                )
            )
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
        TigText(text = state.loadingMessage.asString())
    }
}

@Preview
@Composable
private fun PreviewLoading() {
    Loading(
        TimeRoutinePageUiState.Loading(
            UiText.Res(CommonR.string.message_loading)
        )
    )
}
