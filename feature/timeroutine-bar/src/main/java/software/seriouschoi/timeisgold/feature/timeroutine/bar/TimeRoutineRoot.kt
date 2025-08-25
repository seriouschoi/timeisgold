package software.seriouschoi.timeisgold.feature.timeroutine.bar

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import kotlinx.serialization.Serializable
import software.seriouschoi.navigator.NavigatorDest

@Serializable
data object TimeRoutineBarNavRoot : NavigatorDest

fun NavGraphBuilder.timeRoutineBarSection() {
    navigation<TimeRoutineBarNavRoot>(
        startDestination = TimeRoutineBarNavRoot,
    ) {
        // TODO: time routine sections...
    }
}