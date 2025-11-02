package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots

import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.TimeSlotListState
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.slotedit.TimeSlotEditState

internal data class TimeSlotListPageUiState(
    val slotListState: TimeSlotListState = TimeSlotListState(),
    val editState: TimeSlotEditState? = null,
) {
    val isEditMode: Boolean
        get() = editState != null
}