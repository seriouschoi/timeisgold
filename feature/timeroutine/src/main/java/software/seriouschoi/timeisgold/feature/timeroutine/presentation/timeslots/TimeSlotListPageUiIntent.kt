package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots

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

}

internal enum class TimeSlotUpdateTimeType {
    START, END, START_AND_END
}
