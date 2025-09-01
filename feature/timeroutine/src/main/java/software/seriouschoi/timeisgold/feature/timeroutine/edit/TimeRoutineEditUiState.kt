package software.seriouschoi.timeisgold.feature.timeroutine.edit

import java.time.DayOfWeek

internal sealed interface TimeRoutineEditUiState {
    data class Routine(
        val routineTitle: String = "",
        val dayOfWeekList: List<DayOfWeek> = emptyList(),
        val currentDayOfWeek: DayOfWeek? = null,
    ) : TimeRoutineEditUiState

    data object Loading : TimeRoutineEditUiState
}