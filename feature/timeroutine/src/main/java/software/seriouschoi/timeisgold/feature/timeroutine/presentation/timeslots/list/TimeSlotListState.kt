package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list

import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.item.TimeSlotItemUiState
import software.seriouschoi.timeisgold.core.common.ui.R as CommonR

internal data class TimeSlotListState(
    val slotItemList: List<TimeSlotItemUiState> = emptyList(),
    val loadingMessage: UiText? = null,
    val errorMessage: UiText? = null,
)

internal fun TimeSlotListState.loadingState() : TimeSlotListState{
    return this.copy(
        loadingMessage = UiText.MultipleResArgs.create(
            CommonR.string.message_format_loading,
            CommonR.string.text_routine
        )
    )
}
