package software.seriouschoi.timeisgold.feature.timeroutine.edit

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import software.seriouschoi.navigator.NavigatorRoute

@Serializable
internal data class TimeRoutineEditScreenRoute(
    val dayOfWeekOrdinal: Int,
) : NavigatorRoute {
    companion object {
        fun routes(navGraphBuilder: NavGraphBuilder) {
            navGraphBuilder.composable<TimeRoutineEditScreenRoute> { it: NavBackStackEntry ->
                TimeRoutineEditScreen()
            }
        }
    }
}