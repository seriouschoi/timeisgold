package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.slotedit

import java.time.LocalTime

internal data class TimeSlotEditState(
    val slotUuid: String? = null,
    val title: String = "",
    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,
)
