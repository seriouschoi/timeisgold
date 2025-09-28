package software.seriouschoi.timeisgold.feature.timeroutine.page

internal data class TimeSlotCardUiState(
    val slotUuid: String,
    val routineUuid: String,
    val title: String,
    val startMinutesOfDay: Int,
    val endMinutesOfDay: Int,
    val startMinuteText: String,
    val endMinuteText: String,
    val slotClickIntent: TimeRoutinePageUiIntent,
    val isSelected: Boolean
)