package software.seriouschoi.timeisgold.presentation.feature.timeroutine.set

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import software.seriouschoi.navigator.NavigatorRoute

@Serializable
internal data class SetTimeRoutinePresentationRoute(
    val timeRoutineId: String?
) : NavigatorRoute

internal fun NavGraphBuilder.setTimeRoutine() {
    composable<SetTimeRoutinePresentationRoute> {
        val routineId = it.toRoute<SetTimeRoutinePresentationRoute>().timeRoutineId
        SetTimeRoutineRoute(routineId)
    }
}


@Composable
internal fun SetTimeRoutineRoute(
    timeRoutineId: String?,
    vm: SetTimeRoutineViewModel = hiltViewModel()
) {
    Text("set time routine. id-$timeRoutineId")
}