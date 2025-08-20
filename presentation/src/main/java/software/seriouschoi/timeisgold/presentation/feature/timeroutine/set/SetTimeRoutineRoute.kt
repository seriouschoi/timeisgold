package software.seriouschoi.timeisgold.presentation.feature.timeroutine.set

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
internal data class SetTimeRoutinePresentationDest(
    val timeRoutineId: String?
)

internal fun NavGraphBuilder.setTimeRoutine() {
    composable<SetTimeRoutinePresentationDest> {
        val routineId = it.toRoute<SetTimeRoutinePresentationDest>().timeRoutineId
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