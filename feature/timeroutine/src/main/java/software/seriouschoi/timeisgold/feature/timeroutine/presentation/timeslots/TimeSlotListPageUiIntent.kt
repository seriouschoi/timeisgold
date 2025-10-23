package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots

import java.time.LocalTime

internal sealed interface TimeSlotListPageUiIntent {
    data class ShowSlotEdit(
        val slotId: String? = null,
        val title: String? = null,
        val startTime: LocalTime? = null,
        val endTime: LocalTime? = null,
    ) : TimeSlotListPageUiIntent

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


