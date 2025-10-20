package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots

internal sealed interface TimeRoutinePageUiIntent {
    data class ShowSlotEdit(val slotId: String, val routineId: String) : TimeRoutinePageUiIntent
    data class UpdateTimeSlotUi(
        val uuid: String,
        val minuteFactor: Int,
        val updateTimeType: TimeSlotUpdateTimeType
    ) : TimeRoutinePageUiIntent

    data object UpdateTimeSlotList : TimeRoutinePageUiIntent

}

internal enum class TimeSlotUpdateTimeType {
    START, END, START_AND_END
}


