package software.seriouschoi.timeisgold.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import software.seriouschoi.timeisgold.presentation.feature.home.HomeRoute
import software.seriouschoi.timeisgold.presentation.feature.splash.SplashRoute
import software.seriouschoi.timeisgold.presentation.feature.timeroutine.set.SetTimeRoutineRoute

/**
 * Created by jhchoi on 2025. 8. 19.
 * jhchoi@neofect.com
 */
fun NavGraphBuilder.destSection(navController: NavController) {
    composable<Dest.Splash> {
        SplashRoute()
    }
    composable<Dest.Home> {
        HomeRoute()
    }
    composable<Dest.SetTimeRoutine> { entry ->
        val routineId = entry.toRoute<Dest.SetTimeRoutine>().timeRoutineId
        SetTimeRoutineRoute(routineId)
    }
    composable<Dest.Back> {
        navController.popBackStack()
    }
}