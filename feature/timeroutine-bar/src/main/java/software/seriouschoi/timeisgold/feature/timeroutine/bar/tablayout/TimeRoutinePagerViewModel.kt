package software.seriouschoi.timeisgold.feature.timeroutine.bar.tablayout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import software.seriouschoi.navigator.DestNavigatorPort
import java.time.DayOfWeek
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 8. 26.
 * jhchoi
 */
@HiltViewModel
internal class TimeRoutinePagerViewModel @Inject constructor(
    private val saved: SavedStateHandle,
    private val navigator: DestNavigatorPort,
) : ViewModel() {
    private val _uiState = MutableStateFlow(TimeRoutineTabBarUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val dayOfWeekList = listOf(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY,
            DayOfWeek.SUNDAY
        )

        _uiState.value = TimeRoutineTabBarUiState(
            dayOfWeekList = dayOfWeekList
        )
    }

}

internal data class TimeRoutineTabBarUiState(
    val dayOfWeekList: List<DayOfWeek> = listOf(),
)
