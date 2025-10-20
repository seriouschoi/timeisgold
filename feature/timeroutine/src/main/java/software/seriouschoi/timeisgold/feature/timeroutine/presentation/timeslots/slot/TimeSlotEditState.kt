package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.slot

import java.time.LocalTime

data class TimeSlotEditState(
    val slotUuid: String,
    val title: String,
    val startTime: LocalTime,
    val endTime: LocalTime,
)
