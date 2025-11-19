package software.seriouschoi.timeisgold.feature.timeroutine.presentation.components.dayofweeks.pager

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.time.DayOfWeek
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 10. 14.
 * jhchoi
 */
internal class DayOfWeeksPagerStateHolder @Inject constructor() {

    private val _state = MutableStateFlow(DayOfWeeksPagerState())

    val state: StateFlow<DayOfWeeksPagerState> = _state

    fun select(dayOfWeek: DayOfWeek) {
        _state.update {
            it.copy(
                currentDayOfWeek = dayOfWeek
            )
        }
    }
}