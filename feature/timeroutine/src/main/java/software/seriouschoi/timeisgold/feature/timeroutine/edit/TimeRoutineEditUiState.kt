package software.seriouschoi.timeisgold.feature.timeroutine.edit

import software.seriouschoi.timeisgold.core.common.ui.UiText
import java.time.DayOfWeek

internal sealed interface TimeRoutineEditUiState {
    data class Routine(
        val routineTitle: String = "",
        val dayOfWeekList: Set<DayOfWeek> = emptySet(),
        val currentDayOfWeek: DayOfWeek? = null,
        val routineUuid: String? = null,
        val validState: TimeRoutineEditUiValidUiState = TimeRoutineEditUiValidUiState(),
    ) : TimeRoutineEditUiState

    data object Loading : TimeRoutineEditUiState
}

internal data class TimeRoutineEditUiValidUiState(
    val invalidTitleMessage: UiText? = null,
    val invalidDayOfWeekMessage: UiText? = null,
    val isValid: Boolean = true
)
