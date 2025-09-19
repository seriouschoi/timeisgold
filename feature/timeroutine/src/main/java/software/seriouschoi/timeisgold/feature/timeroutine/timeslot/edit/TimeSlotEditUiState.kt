package software.seriouschoi.timeisgold.feature.timeroutine.timeslot.edit

import java.time.LocalTime

internal data class TimeSlotEditUiState(
    val slotName: String = "",
    val visibleDelete: Boolean = false,
    val startTime: LocalTime = LocalTime.now(),
    val endTime: LocalTime = LocalTime.now(),
    val loading: Boolean = false
)
