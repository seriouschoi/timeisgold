package software.seriouschoi.timeisgold.feature.timeroutine.page

import java.time.LocalTime

internal data class TimeRoutinePageSlotItemUiState(
    val uuid: String,
    val title: String,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val slotClickIntent: TimeRoutinePageUiIntent,
)