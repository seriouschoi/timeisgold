package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.slotedit

import java.time.LocalTime

internal data class TimeSlotEditState(
    val slotUuid: String,
    val title: String,
    val startTime: LocalTime,
    val endTime: LocalTime,
)
