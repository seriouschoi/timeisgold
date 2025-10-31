package software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager

import software.seriouschoi.timeisgold.feature.timeroutine.presentation.components.dayofweeks.check.DayOfWeeksCheckIntent
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.components.dayofweeks.pager.DayOfWeeksPagerStateIntent

internal sealed interface TimeRoutinePagerUiIntent {
    data class LoadRoutine(val stateIntent: DayOfWeeksPagerStateIntent) : TimeRoutinePagerUiIntent


    data class UpdateRoutineTitle(val title: String) : TimeRoutinePagerUiIntent
    data class CheckDayOfWeek(
        val dayOfWeekCheckIntent: DayOfWeeksCheckIntent
    ) : TimeRoutinePagerUiIntent
}
