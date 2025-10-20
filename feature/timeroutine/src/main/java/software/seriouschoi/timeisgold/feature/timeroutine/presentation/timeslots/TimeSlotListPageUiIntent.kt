package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots

internal sealed interface TimeSlotListPageUiIntent {
    data class ShowSlotEdit(val slotId: String, val routineId: String) : TimeSlotListPageUiIntent
    data class UpdateTimeSlotUi(
        val uuid: String,
        val minuteFactor: Int,
        val updateTimeType: TimeSlotUpdateTimeType
    ) : TimeSlotListPageUiIntent

    data object UpdateTimeSlotList : TimeSlotListPageUiIntent

}

internal enum class TimeSlotUpdateTimeType {
    START, END, START_AND_END
}


