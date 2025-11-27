package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots

import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.item.TimeSlotItemUiState
import java.time.LocalTime

internal sealed interface TimeSlotListPageUiIntent {

    data class DragTimeSlotHeader(
        val slotId: String,
        val minuteFactor: Int,
    ) : TimeSlotListPageUiIntent

    data class DragTimeSlotFooter(
        val slotId: String,
        val minuteFactor: Int,
    ) : TimeSlotListPageUiIntent

    data class DragTimeSlotBody(
        val slotId: String,
        val minuteFactor: Int,
    ) : TimeSlotListPageUiIntent

    data object ApplyTimeSlotListChanges : TimeSlotListPageUiIntent

    data object SlotEditCancel : TimeSlotListPageUiIntent

    data class ChangeSelectedTimeSlotTitle(
        val title: String,
    ): TimeSlotListPageUiIntent

    data class ChangeSelectedTimeSlotStartTime(
        val startTime: LocalTime
    ): TimeSlotListPageUiIntent

    data class ChangeSelectedTimeSlotEndTime(
        val endTime: LocalTime
    ): TimeSlotListPageUiIntent

    data class SelectTimeSlot(
        val slot: TimeSlotItemUiState,
    ): TimeSlotListPageUiIntent

    data class SelectTimeSlice(
        val hourOfDay: Int,
    ): TimeSlotListPageUiIntent

    data class DeleteTimeSlot(
        val slotId: String
    ): TimeSlotListPageUiIntent

}
