package software.seriouschoi.timeisgold.presentation.feature.home

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import software.seriouschoi.timeisgold.presentation.navigation.PresentationDest


@Serializable
internal data object HomePresentationDest : PresentationDest

internal fun NavGraphBuilder.home() {
    composable<HomePresentationDest> {
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

