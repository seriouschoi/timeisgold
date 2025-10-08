package software.seriouschoi.timeisgold.feature.timeroutine

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import kotlinx.serialization.Serializable
import software.seriouschoi.navigator.NavigatorRoute
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.edit.TimeRoutineEditScreenRoute
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager.TimeRoutinePagerScreenRoute
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.edit.TimeSlotEditScreenRoute

@Serializable
data object TimeRoutineBarNavRoot : NavigatorRoute

fun NavGraphBuilder.timeRoutineBarSection() {
    navigation<TimeRoutineBarNavRoot>(
        startDestination = TimeRoutinePagerScreenRoute,
    ) {
        TimeRoutinePagerScreenRoute.routes(this)
        TimeRoutineEditScreenRoute.Companion.routes(this)
        TimeSlotEditScreenRoute.Companion.routes(this)
    }
}