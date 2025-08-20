package software.seriouschoi.timeisgold.presentation.feature.splash

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import software.seriouschoi.timeisgold.presentation.navigation.PresentationDest

@Serializable
internal object SplashPresentationDest : PresentationDest

@Composable
internal fun SplashRoute(vm: SplashViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) { vm.onLaunch() }
    Text("Splash...")
}

internal fun NavGraphBuilder.splash() {
    composable<SplashPresentationDest> {
        SplashRoute()
    }
}

