package software.seriouschoi.timeisgold.presentation.feature.home

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import software.seriouschoi.navigator.NavigatorRoute


@Serializable
internal data object HomePresentationRoute : NavigatorRoute

internal fun NavGraphBuilder.home() {
    composable<HomePresentationRoute> {
        HomeRoute()
    }
}

@Composable
internal fun HomeRoute(
    vm: HomeViewModel = hiltViewModel()
) {
    Button(
        onClick = {
            vm.openSetTimeRoutine()
        }
    ) {
        Text("Set Time Routine test_routine")
    }
}

