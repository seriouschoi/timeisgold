package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots

import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.TimeSlotItemUiState
import software.seriouschoi.timeisgold.core.common.ui.R as CommonR

internal sealed interface TimeSlotListPageUiState {
    data class Data(
        val slotItemList: List<TimeSlotItemUiState> = emptyList(),
        val loadingMessage: UiText? = null
    ) : TimeSlotListPageUiState

    data class Error(
        val errorMessage: UiText,
        val confirmButton: TimeSlotListPageButtonState? = null
    ) : TimeSlotListPageUiState
}

internal data class TimeSlotListPageButtonState(
    val buttonLabel: UiText,
    val intent: TimeRoutinePageUiIntent,
)

internal fun TimeSlotListPageUiState.Data.loadingState() : TimeSlotListPageUiState.Data{
    return this.copy(
        loadingMessage = UiText.MultipleResArgs.create(
            CommonR.string.message_format_loading,
            CommonR.string.text_routine
        )
    )
}