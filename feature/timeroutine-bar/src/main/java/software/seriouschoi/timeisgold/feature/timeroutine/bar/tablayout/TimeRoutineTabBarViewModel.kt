package software.seriouschoi.timeisgold.feature.timeroutine.bar.tablayout

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.DayOfWeek
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 8. 26.
 * jhchoi
 */
@HiltViewModel
internal class TimeRoutineTabBarViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(TimeRoutineTabBarUiState())
    val uiState = _uiState.asStateFlow()

    init {
        DayOfWeek.entries.map {
            // TODO: jhchoi 2025. 8. 26. common-ui 모듈에 UiText라는 클래스를 정의하고,
            /*
            common-ui 모듈에 UiText라는 클래스를 정의하고,
            이걸 구현하는 형태로 만든다. 즉 문자열 리소스를 직접 담지 말고, 추상화한다.
             */
            TimeRoutineDayOfWeekItem(
                dayOfWeek = it,
                name = it.name
            )
        }.let {
            _uiState.value = _uiState.value.copy(
                timeRoutineList = it
            )
        }
    }

}

internal data class TimeRoutineTabBarUiState(
    val timeRoutineList: List<TimeRoutineDayOfWeekItem> = listOf()
)

internal data class TimeRoutineDayOfWeekItem(
    val dayOfWeek: DayOfWeek,
    val name: String
)