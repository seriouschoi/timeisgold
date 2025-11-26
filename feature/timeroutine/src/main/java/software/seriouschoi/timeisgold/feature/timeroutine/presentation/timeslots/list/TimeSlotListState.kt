package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list

import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.item.TimeSlotItemUiState

internal data class TimeSlotListState(
    val slotItemList: List<TimeSlotItemUiState> = emptyList(),
    val loadingMessage: UiText? = null,
    val errorMessage: UiText? = null
)