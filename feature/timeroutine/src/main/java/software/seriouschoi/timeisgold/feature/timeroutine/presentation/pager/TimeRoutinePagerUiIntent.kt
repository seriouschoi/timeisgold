package software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager

import java.time.DayOfWeek

sealed interface TimeRoutinePagerUiIntent {
    data class LoadRoutine(val dayOfWeek: DayOfWeek) : TimeRoutinePagerUiIntent
    object ModifyRoutine : TimeRoutinePagerUiIntent
    object AddRoutine : TimeRoutinePagerUiIntent
}
