package software.seriouschoi.timeisgold.feature.timeroutine.edit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Composable
internal fun TimeRoutineEditScreen() {

    Column {
        Box {
            TimeRoutineLoading()
            TimeRoutineEdit()
        }
    }
}

@Composable
private fun TimeRoutineLoading() {
    val viewModel = hiltViewModel<TimeRoutineEditViewModel>()

    val loading by remember(viewModel) {
        viewModel.uiState.map { it: TimeRoutineEditUiState ->
            (it as? TimeRoutineEditUiState.Loading) != null
        }.distinctUntilChanged()
    }.collectAsState(false)

    if (!loading)
        return

    CircularProgressIndicator()
}

@Composable
private fun TimeRoutineEdit() {
    val viewModel = hiltViewModel<TimeRoutineEditViewModel>()

    val currentRoutine by remember(viewModel) {
        viewModel.uiState.map { it: TimeRoutineEditUiState ->
            (it as? TimeRoutineEditUiState.Routine)
        }.distinctUntilChanged()
    }.collectAsState(TimeRoutineEditUiState.Routine())

    currentRoutine ?: return

    Column {
        Text("timeRoutineEditScreen. ${currentRoutine?.currentDayOfWeek}")
        TextField(
            value = currentRoutine?.routineTitle ?: "",
            onValueChange = {
                viewModel.updateRoutineTitle(it)
            }
        )
    }
}