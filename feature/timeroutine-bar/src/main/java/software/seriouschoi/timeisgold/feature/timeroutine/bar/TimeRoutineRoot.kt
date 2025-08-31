package software.seriouschoi.timeisgold.feature.timeroutine.bar

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import kotlinx.serialization.Serializable
import software.seriouschoi.navigator.NavigatorDest
import software.seriouschoi.timeisgold.feature.timeroutine.bar.create.TimeRoutineEditScreenDest
import software.seriouschoi.timeisgold.feature.timeroutine.bar.tablayout.TimeRoutinePagerScreenDest

@Serializable
data object TimeRoutineBarNavRoot : NavigatorDest

fun NavGraphBuilder.timeRoutineBarSection() {
    navigation<TimeRoutineBarNavRoot>(
        startDestination = TimeRoutinePagerScreenDest,
    ) {
        TimeRoutinePagerScreenDest.routes(this)
        TimeRoutineEditScreenDest.routes(this)
    }
}