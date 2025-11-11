package software.seriouschoi.timeisgold.feature.timeroutine.presentation.components.dayofweeks.pager

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 10. 14.
 * jhchoi
 */
internal class DayOfWeeksPagerStateHolder @Inject constructor() {

    private val _state = MutableStateFlow(
        DayOfWeeksPagerState(
            dayOfWeeks = DAY_OF_WEEKS,
            currentDayOfWeek = DayOfWeek.from(LocalDate.now())
        )
    )

    val state: StateFlow<DayOfWeeksPagerState> = _state

    fun select(dayOfWeek: DayOfWeek) {
        _state.update {
            it.copy(
                currentDayOfWeek = dayOfWeek
            )
        }
    }

    companion object {
        val DAY_OF_WEEKS = listOf(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY,
            DayOfWeek.SUNDAY
        )
        val DEFAULT_DAY_OF_WEEK: DayOfWeek = DayOfWeek.from(LocalDate.now())
    }
}