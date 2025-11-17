package software.seriouschoi.timeisgold.feature.timeroutine.presentation.components.dayofweeks.pager

import java.time.DayOfWeek

internal data class DayOfWeeksPagerState(
    val dayOfWeeks: List<DayOfWeek> = DayOfWeeksPagerStateHolder.Companion.DAY_OF_WEEKS,
    val currentDayOfWeek: DayOfWeek = DayOfWeeksPagerStateHolder.Companion.DEFAULT_DAY_OF_WEEK
)