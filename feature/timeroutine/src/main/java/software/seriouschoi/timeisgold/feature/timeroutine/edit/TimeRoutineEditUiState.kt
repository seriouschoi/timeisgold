package software.seriouschoi.timeisgold.feature.timeroutine.edit

import java.time.DayOfWeek

internal sealed interface TimeRoutineEditUiState {
    data class Routine(
        val routineTitle: String = "",
        val dayOfWeekList: Set<DayOfWeek> = emptySet(),
        val currentDayOfWeek: DayOfWeek? = null,
        val routineUuid: String? = null,
        val isValid: Boolean = false,
    ) : TimeRoutineEditUiState

    data object Loading : TimeRoutineEditUiState
}