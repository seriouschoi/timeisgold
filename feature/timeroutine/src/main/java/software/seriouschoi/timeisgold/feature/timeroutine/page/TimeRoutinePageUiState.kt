package software.seriouschoi.timeisgold.feature.timeroutine.page

import java.time.DayOfWeek

internal sealed interface TimeRoutinePageUiState {
    data class Routine(
        val title: String = "",
        val slotItemList: List<TimeRoutinePageSlotItemUiState> = emptyList(),
        val dayOfWeeks: List<DayOfWeek> = listOf(),
    ) : TimeRoutinePageUiState

    object Empty : TimeRoutinePageUiState
    object Loading : TimeRoutinePageUiState
    object Error : TimeRoutinePageUiState
}