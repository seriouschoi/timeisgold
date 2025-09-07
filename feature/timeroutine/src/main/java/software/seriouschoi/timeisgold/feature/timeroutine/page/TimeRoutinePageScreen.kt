package software.seriouschoi.timeisgold.feature.timeroutine.page

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import software.seriouschoi.timeisgold.feature.timeroutine.bar.R
import software.seriouschoi.timeisgold.feature.timeroutine.page.TimeRoutinePageUiIntent.CreateRoutine
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
    // TODO: dayOfWeek를 파라미터로 들고 있지 말고, 뷰모델에서 상태로 가져오자.
    val viewModel = hiltViewModel<TimeRoutinePageViewModel>(key = dayOfWeek.name)

    val title by remember {
        viewModel.uiState.map {
            (it as? TimeRoutinePageUiState.Routine)?.title ?: ""
        }.distinctUntilChanged()
    }.collectAsState(initial = "")

    LaunchedEffect(dayOfWeek) {
        viewModel.load(dayOfWeek)
    }


    val uiState by remember {
        viewModel.uiState
    }.collectAsState(TimeRoutinePageUiState.Empty)

    val currentState = uiState

    when (currentState) {
        is TimeRoutinePageUiState.Loading -> {
            Column(modifier = modifier) {
                Text(text = stringResource(R.string.message_routine_loading, dayOfWeek.name))
            }
        }

        is TimeRoutinePageUiState.Empty -> {
            Column(modifier = modifier) {
                Text(text = stringResource(R.string.message_routine_create_confirm, dayOfWeek.name))
                Button(
                    onClick = {
                        viewModel.sendIntent(
                            CreateRoutine(dayOfWeek)
                        )
                    }
                ) {
                    Text(text = stringResource(CommonR.string.text_create))
                }
            }
        }

        is TimeRoutinePageUiState.Routine -> {
            Column(modifier = modifier) {
                Text(text = dayOfWeek.name)
                Text(text = title)
            }
        }

        TimeRoutinePageUiState.Error -> {
            Column(modifier = modifier) {
                Text(text = stringResource(CommonR.string.message_failed_load_data))
            }
        }
    }
}