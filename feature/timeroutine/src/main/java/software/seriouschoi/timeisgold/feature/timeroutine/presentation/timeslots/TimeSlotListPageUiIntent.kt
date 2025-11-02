package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots

import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.item.TimeSlotItemUiState
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.slotedit.TimeSlotEditStateIntent

internal sealed interface TimeSlotListPageUiIntent {

    data class UpdateTimeSlotUi(
        val uuid: String,
        val minuteFactor: Int,
        val updateTimeType: TimeSlotUpdateTimeType,
    ) : TimeSlotListPageUiIntent

    data object UpdateTimeSlotList : TimeSlotListPageUiIntent

    data object Cancel : TimeSlotListPageUiIntent

    data class UpdateTimeSlotEdit(
        val slotEditState: TimeSlotEditStateIntent,
    ) : TimeSlotListPageUiIntent

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
