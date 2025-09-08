package software.seriouschoi.timeisgold.feature.timeroutine.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.core.common.ui.asString
import java.time.DayOfWeek
import software.seriouschoi.timeisgold.core.common.ui.R as CommonR

/**
 * Created by jhchoi on 2025. 8. 26.
 * jhchoi
 */
@Composable
fun TimeRoutinePageScreen(
    modifier: Modifier,
    dayOfWeek: DayOfWeek,
) {
    val viewModel = hiltViewModel<TimeRoutinePageViewModel>(key = dayOfWeek.name)

    LaunchedEffect(dayOfWeek) {
        viewModel.load(dayOfWeek)
    }

    val uiState by remember {
        viewModel.uiState
    }.collectAsState(TimeRoutinePageUiState.Loading(
        UiText.Res(CommonR.string.message_loading)
    ))

    val currentState = uiState

    when (currentState) {
        is TimeRoutinePageUiState.Loading -> {
            Loading(currentState)
        }

        is TimeRoutinePageUiState.Empty -> {
            Column(modifier = modifier) {
                Text(text = currentState.emptyMessage.asString())
                Button(
                    onClick = {
                        viewModel.sendIntent(
                            TimeRoutinePageUiIntent.CreateRoutine
                        )
                    }
                ) {
                    Text(text = stringResource(CommonR.string.text_create))
                }
            }
        }

        is TimeRoutinePageUiState.Routine -> {
            Column(modifier = modifier) {
                Text(text = currentState.dayOfWeekName)
                Text(text = currentState.title)
            }
        }

        is TimeRoutinePageUiState.Error -> {
            Column(modifier = modifier) {
                Text(text = currentState.errorMessage.asString())
            }
        }
    }
}

@Preview
@Composable
private fun PreviewLoading() {
    Loading(TimeRoutinePageUiState.Loading(
        UiText.Res(CommonR.string.message_loading)
    ))
}

@Composable
private fun Loading(state: TimeRoutinePageUiState.Loading) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = state.loadingMessage.asString())
    }
}