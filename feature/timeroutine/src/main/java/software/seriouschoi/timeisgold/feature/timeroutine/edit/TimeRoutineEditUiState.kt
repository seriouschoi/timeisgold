package software.seriouschoi.timeisgold.feature.timeroutine.edit

import software.seriouschoi.timeisgold.core.common.ui.UiText
import java.time.DayOfWeek

data class TimeRoutineEditUiState(
    val routineTitle: String = "",
    val dayOfWeekList: Set<DayOfWeek> = emptySet(),
    val currentDayOfWeek: DayOfWeek? = null,
    val visibleDelete: Boolean = false,
    val isLoading: Boolean = false
) {

}

internal data class TimeRoutineEditUiValidUiState(
    val invalidTitleMessage: UiText? = null,
    val invalidDayOfWeekMessage: UiText? = null,
    val isValid: Boolean = false
)
