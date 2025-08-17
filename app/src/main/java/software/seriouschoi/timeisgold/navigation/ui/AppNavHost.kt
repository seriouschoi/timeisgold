package software.seriouschoi.timeisgold.navigation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import software.seriouschoi.timeisgold.presentation.navigation.Dest
import software.seriouschoi.timeisgold.presentation.navigation.destSection

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
) {
    val viewModel: AppNavHostViewModel = hiltViewModel()

    DisposableEffect(navController) {
        viewModel.destNavigatorPort.setControllerProvider { navController }
        onDispose {
            viewModel.destNavigatorPort.setControllerProvider(null)
        }
    }

    NavHost(
        navController = navController,
        startDestination = Dest.Splash
    ) {
        destSection(navController)
    }
}