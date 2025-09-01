package software.seriouschoi.timeisgold.feature.timeroutine

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import kotlinx.serialization.Serializable
import software.seriouschoi.navigator.NavigatorDest
import software.seriouschoi.timeisgold.feature.timeroutine.edit.TimeRoutineEditScreenDest
import software.seriouschoi.timeisgold.feature.timeroutine.pager.TimeRoutinePagerScreenDest

@Serializable
data object TimeRoutineBarNavRoot : NavigatorDest

fun NavGraphBuilder.timeRoutineBarSection() {
    navigation<TimeRoutineBarNavRoot>(
        startDestination = TimeRoutinePagerScreenDest,
    ) {
        TimeRoutinePagerScreenDest.routes(this)
        TimeRoutineEditScreenDest.Companion.routes(this)
    }
}