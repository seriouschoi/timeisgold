package software.seriouschoi.timeisgold.presentation.feature.splash

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SplashRoute(vm: SplashViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) { vm.onLaunch() }
    Text("Splash...")
}