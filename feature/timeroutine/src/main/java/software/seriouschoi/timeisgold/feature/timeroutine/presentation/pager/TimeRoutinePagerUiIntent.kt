package software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager

import java.time.DayOfWeek

internal sealed interface TimeRoutinePagerUiIntent {
    data class SelectCurrentDayOfWeek(val currentDayOfWeek: DayOfWeek) : TimeRoutinePagerUiIntent
    data class UpdateRoutineTitle(val title: String) : TimeRoutinePagerUiIntent

    data class CheckDayOfWeek(
        val dayOfWeek: DayOfWeek, val isCheck: Boolean
    ) : TimeRoutinePagerUiIntent
}