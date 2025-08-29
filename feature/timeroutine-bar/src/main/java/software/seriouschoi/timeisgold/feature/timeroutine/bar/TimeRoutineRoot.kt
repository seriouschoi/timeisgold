package software.seriouschoi.timeisgold.feature.timeroutine.bar

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import kotlinx.serialization.Serializable
import software.seriouschoi.navigator.NavigatorDest
import software.seriouschoi.timeisgold.feature.timeroutine.bar.tablayout.TimeRoutineTabBarScreenDest
import software.seriouschoi.timeisgold.feature.timeroutine.bar.tablayout.tabBar

@Serializable
data object TimeRoutineBarNavRoot : NavigatorDest

fun NavGraphBuilder.timeRoutineBarSection() {
    navigation<TimeRoutineBarNavRoot>(
        startDestination = TimeRoutineTabBarScreenDest,
    ) {
        // TODO: time routine sections...
        tabBar()
    }
}