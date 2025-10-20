package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots

import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.TimeSlotItemUiState
import software.seriouschoi.timeisgold.core.common.ui.R as CommonR

internal data class TimeSlotListPageUiState(
    val slotItemList: List<TimeSlotItemUiState> = emptyList(),
    val loadingMessage: UiText? = null,
    val errorMessage: UiText? = null
)

internal fun TimeSlotListPageUiState.loadingState() : TimeSlotListPageUiState{
    return this.copy(
        loadingMessage = UiText.MultipleResArgs.create(
            CommonR.string.message_format_loading,
            CommonR.string.text_routine
        )
    )
}