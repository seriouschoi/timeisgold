package software.seriouschoi.timeisgold.feature.timeroutine.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import software.seriouschoi.timeisgold.core.common.ui.TigTheme
import software.seriouschoi.timeisgold.core.common.ui.TigThemePreview
import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.core.common.ui.asString
import software.seriouschoi.timeisgold.core.common.ui.components.TigLabelButton
import software.seriouschoi.timeisgold.core.common.ui.container.TigBlurContainer
import software.seriouschoi.timeisgold.core.common.util.asFormattedString
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Routine(
    state: TimeRoutinePageUiState.Routine,
    sendIntent: (TimeRoutinePageUiIntent) -> Unit,
) {
    val slotItems by remember { mutableStateOf(state.slotItemList) }
    TigBlurContainer {
        LazyColumn {
            items(slotItems) { item ->
                TimeSlot(
                    item,
                    sendIntent
                )
            }
        }
    }
}

@Composable
private fun TimeSlot(
    slotItem: TimeRoutinePageSlotItemUiState,
    sendIntent: (TimeRoutinePageUiIntent) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
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
                TimeRoutinePageSlotItemUiState(
                    title = "타이틀",
                    startTime = LocalTime.now(),
                    endTime = LocalTime.now(),
                    slotClickIntent = TimeRoutinePageUiIntent.CreateRoutine
                )
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
                slotItemList = listOf(),
                dayOfWeeks = listOf()
            )
        ) {

        }
    }
}

@Composable
private fun Error(currentState: TimeRoutinePageUiState.Error) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = currentState.errorMessage.asString())
    }
}

@Composable
@Preview
private fun PreviewError() {
    Error(
        TimeRoutinePageUiState.Error(
            errorMessage = UiText.Raw("알 수 없는 오류.")
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
        Text(text = currentState.emptyMessage.asString())
        TigLabelButton(
            label = stringResource(CommonR.string.text_create),
            onClick = {
                sendIntent(TimeRoutinePageUiIntent.CreateRoutine)
            },
        )
    }
}

@Preview
@Composable
private fun PreviewEmpty() {
    Empty(
        currentState = TimeRoutinePageUiState.Empty(
            emptyMessage = UiText.Raw("시간표를 만들까요?")
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
