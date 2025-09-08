package software.seriouschoi.timeisgold.feature.timeroutine.page

import software.seriouschoi.timeisgold.core.common.ui.UiText
import java.time.DayOfWeek

internal sealed interface TimeRoutinePageUiState {
    data class Routine(
        val title: String = "",
        val dayOfWeekName: String,
        val slotItemList: List<TimeRoutinePageSlotItemUiState> = emptyList(),
        val dayOfWeeks: List<DayOfWeek> = listOf(),
    ) : TimeRoutinePageUiState

    data class Empty(
        val emptyMessage: UiText,
    ) : TimeRoutinePageUiState

    data class Loading(
        val loadingMessage: UiText,
    ) : TimeRoutinePageUiState

    data class Error(
        val errorMessage: UiText,
    ) : TimeRoutinePageUiState
}