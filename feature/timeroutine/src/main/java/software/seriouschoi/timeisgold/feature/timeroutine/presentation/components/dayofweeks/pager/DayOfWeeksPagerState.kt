package software.seriouschoi.timeisgold.feature.timeroutine.presentation.components.dayofweeks.pager

import java.time.DayOfWeek
import java.time.LocalDate

internal data class DayOfWeeksPagerState(
    val dayOfWeeks: List<DayOfWeek> = DAY_OF_WEEKS,
    val currentDayOfWeek: DayOfWeek = LocalDate.now().dayOfWeek
) {
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
    }
}