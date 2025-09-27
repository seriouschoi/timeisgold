package software.seriouschoi.timeisgold.feature.timeroutine.page

import java.time.LocalTime

@Deprecated("use new timeslotcardState")
internal data class TimeSlotCardUiState(
    val uuid: String,
    val title: String,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val slotClickIntent: TimeRoutinePageUiIntent,
)

internal data class NewTimeSlotCardUiState(
    val slotUuid: String,
    val routineUuid: String,
    val title: String,
    val startMinutesOfDay: Int,
    val endMinutesOfDay: Int,
    val startMinuteText: String,
    val endMinuteText: String,
    val slotClickIntent: TimeRoutinePageUiIntent,
)


