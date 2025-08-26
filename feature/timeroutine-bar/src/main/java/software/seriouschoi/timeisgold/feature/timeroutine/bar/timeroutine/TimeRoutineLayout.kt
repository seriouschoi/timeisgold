package software.seriouschoi.timeisgold.feature.timeroutine.bar.timeroutine

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
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

    val title by remember {
        viewModel.uiState.map {
            (it as? TimeRoutineUiState.Routine)?.title ?: ""
        }.distinctUntilChanged()
    }.collectAsState(initial = "")

    LaunchedEffect(dayOfWeek) {
        viewModel.readTimeRoutine(dayOfWeek)
    }

    // TODO: jhchoi 2025. 8. 26. 구현.
    /*
    일단 루틴이 있나 없나 체크해야함.
    루틴이 있으면, 루틴 이름을 보여주고, 없으면, 루틴을 만들까요? 라는 메시지를 보여줌.
    이거를 uiState의 타입으로 만들자.
     */
    Column(modifier = modifier) {
        Text(text = title)
    }

}