package software.seriouschoi.timeisgold.presentation.feature.home

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomeRoute(
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