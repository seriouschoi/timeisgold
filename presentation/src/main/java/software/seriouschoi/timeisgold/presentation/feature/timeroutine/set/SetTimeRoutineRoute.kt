package software.seriouschoi.timeisgold.presentation.feature.timeroutine.set

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SetTimeRoutineRoute(
    timeRoutineId: String?,
    vm: SetTimeRoutineViewModel = hiltViewModel()
) {
    Text("set time routine. id-$timeRoutineId")
}