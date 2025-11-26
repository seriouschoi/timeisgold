package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots

import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.item.TimeSlotItemUiState
import java.time.LocalTime

internal sealed interface TimeSlotListPageUiIntent {

    data class DragTimeSlot(
        val slotId: String,
        val minuteFactor: Int,
        val updateTimeType: TimeSlotUpdateTimeType,
    ) : TimeSlotListPageUiIntent

    data object ApplyTimeSlotListChanges : TimeSlotListPageUiIntent

    data object SlotEditCancel : TimeSlotListPageUiIntent

    data class ActiveSlotTitleEdit(
        val title: String,
    ): TimeSlotListPageUiIntent

    data class ActiveSlotSetStartTime(
        val startTime: LocalTime
    ): TimeSlotListPageUiIntent

    data class ActiveSlotSetEndTime(
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

internal enum class TimeSlotUpdateTimeType {
    START, END, START_AND_END
}
