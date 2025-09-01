package software.seriouschoi.timeisgold.feature.timeroutine.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import software.seriouschoi.navigator.NavigatorDest
import java.time.DayOfWeek

@Serializable
internal data class TimeRoutineEditScreenDest(
    val dayOfWeek: DayOfWeek,
) : NavigatorDest {
    companion object {
        fun routes(navGraphBuilder: NavGraphBuilder) {
            navGraphBuilder.composable<TimeRoutineEditScreenDest> { it: NavBackStackEntry ->
                val route = it.toRoute<TimeRoutineEditScreenDest>()
                TimeRoutineEditScreen(route)
            }
        }
    }
}

@Composable
internal fun TimeRoutineEditScreen(
    dest: TimeRoutineEditScreenDest,
) {
    val viewModel = hiltViewModel<TimeRoutineEditViewModel>()
    remember(viewModel) {
        viewModel.uiState.map { it: TimeRoutineEditUiState ->
            (it as? TimeRoutineEditUiState.Routine)?.routineTitle
        }
    }

    Column {
        Text("timeRoutineEditScreen. ${dest.dayOfWeek.name}")
    }
}