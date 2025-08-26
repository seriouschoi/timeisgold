package software.seriouschoi.timeisgold.feature.timeroutine.bar.timeroutine

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.DayOfWeek

/**
 * Created by jhchoi on 2025. 8. 26.
 * jhchoi
 */
@Composable
fun TimeRoutineLayout(
    modifier: Modifier,
    dayOfWeek: DayOfWeek
) {
    val viewModel = hiltViewModel<TimeRoutineViewModel>()

    // TODO: jhchoi 2025. 8. 26. 구현.
    Column(modifier = modifier) {
        Text(text = dayOfWeek.toString())
    }

}