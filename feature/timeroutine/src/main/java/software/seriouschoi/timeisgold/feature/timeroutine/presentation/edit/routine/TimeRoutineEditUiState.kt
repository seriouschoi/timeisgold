package software.seriouschoi.timeisgold.feature.timeroutine.presentation.edit.routine

import software.seriouschoi.timeisgold.core.common.ui.UiText
import java.time.DayOfWeek

data class TimeRoutineEditUiState(
    val routineTitle: String = "",
    val subTitle: String = "",
    val dayOfWeekMap: Map<DayOfWeek, TimeRoutineEditDayOfWeekItemState> = TimeRoutineEditDayOfWeekItemState.createDefaultItemMap(),
    val currentDayOfWeek: DayOfWeek? = null,
    val visibleDelete: Boolean = false,
    val isLoading: Boolean = false,
)

data class TimeRoutineEditDayOfWeekItemState(
    val dayOfWeek: DayOfWeek,
    val checked: Boolean = false,
    val enable: Boolean = true,
) {
    companion object {
        fun createDefaultItemMap(): Map<DayOfWeek, TimeRoutineEditDayOfWeekItemState> {
            return DayOfWeek.entries.associateWith {
                TimeRoutineEditDayOfWeekItemState(
                    dayOfWeek = it
                )
            }
        }
    }
}

internal data class TimeRoutineEditUiValidUiState(
    val invalidTitleMessage: UiText? = null,
    val invalidDayOfWeekMessage: UiText? = null,
    val isValid: Boolean = false,
)
