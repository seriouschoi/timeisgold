package software.seriouschoi.timeisgold.feature.timeroutine.pager

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import software.seriouschoi.navigator.DestNavigatorPort
import java.time.DayOfWeek
import java.time.LocalDateTime
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
    private val _uiState = MutableStateFlow(TimeRoutinePagerUiState())

    val uiState: StateFlow<TimeRoutinePagerUiState> = _uiState.asStateFlow()
    init {
        _uiState.value = TimeRoutinePagerUiState(
            dayOfWeekList = DAY_OF_WEEKS,
            today = LocalDateTime.now().dayOfWeek
        )
    }

    companion object {
        private val DAY_OF_WEEKS = listOf(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY,
            DayOfWeek.SUNDAY
        )
    }
}

internal data class TimeRoutinePagerUiState(
    val dayOfWeekList: List<DayOfWeek> = listOf(),
    val today: DayOfWeek = LocalDateTime.now().dayOfWeek
)


