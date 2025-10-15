package software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager.stateholder

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager.stateholder.DayOfWeeksPagerStateHolder.Companion.DAY_OF_WEEKS
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager.stateholder.DayOfWeeksPagerStateHolder.Companion.DEFAULT_DAY_OF_WEEK
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

    val currentDayOfWeek = state.map { it.currentDayOfWeek }

    fun reduce(intent: DayOfWeeksPagerStateIntent) {
        when (intent) {
            is DayOfWeeksPagerStateIntent.Select -> {
                _state.update {
                    it.copy(
                        currentDayOfWeek = intent.dayOfWeek
                    )
                }
            }
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

internal data class DayOfWeeksPagerState(
    val dayOfWeeks: List<DayOfWeek> = DAY_OF_WEEKS,
    val currentDayOfWeek: DayOfWeek = DEFAULT_DAY_OF_WEEK
)

internal sealed interface DayOfWeeksPagerStateIntent {

    data class Select(
        val dayOfWeek: DayOfWeek
    ) : DayOfWeeksPagerStateIntent
}