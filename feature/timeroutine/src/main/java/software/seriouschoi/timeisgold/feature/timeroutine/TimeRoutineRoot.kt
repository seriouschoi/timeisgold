package software.seriouschoi.timeisgold.feature.timeroutine

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import kotlinx.serialization.Serializable
import software.seriouschoi.navigator.NavigatorRoute
import software.seriouschoi.timeisgold.feature.timeroutine.edit.TimeRoutineEditScreenRoute
import software.seriouschoi.timeisgold.feature.timeroutine.pager.TimeRoutinePagerScreenRoute
import software.seriouschoi.timeisgold.feature.timeroutine.timeslot.edit.TimeSlotEditScreenRoute

@Serializable
data object TimeRoutineBarNavRoot : NavigatorRoute

fun NavGraphBuilder.timeRoutineBarSection() {
    navigation<TimeRoutineBarNavRoot>(
        startDestination = TimeRoutinePagerScreenRoute,
    ) {
        TimeRoutinePagerScreenRoute.routes(this)
        TimeRoutineEditScreenRoute.Companion.routes(this)
        TimeSlotEditScreenRoute.routes(this)
    }
}