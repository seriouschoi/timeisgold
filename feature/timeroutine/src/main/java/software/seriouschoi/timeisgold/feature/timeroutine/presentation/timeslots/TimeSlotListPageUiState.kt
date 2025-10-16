package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots

import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.TimeSlotItemUiState
import software.seriouschoi.timeisgold.core.common.ui.R as CommonR

internal sealed interface TimeSlotListPageUiState {
    data class Data(
        val slotItemList: List<TimeSlotItemUiState> = emptyList(),
        val loadingMessage: UiText? = null,
        val errorState: TimeSlotListPageErrorState? = null
    ) : TimeSlotListPageUiState
}

internal data class TimeSlotListPageErrorState(
    val errorMessage: UiText,
    val confirmIntent: TimeRoutinePageUiIntent? = null
)

internal fun TimeSlotListPageUiState.Data.loadingState() : TimeSlotListPageUiState.Data{
    return this.copy(
        loadingMessage = UiText.MultipleResArgs.create(
            CommonR.string.message_format_loading,
            CommonR.string.text_routine
        )
    )
}