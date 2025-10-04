package software.seriouschoi.timeisgold.feature.timeroutine.pager.routine

import java.time.DayOfWeek

/**
 * Created by jhchoi on 2025. 10. 4.
 * jhchoi
 */
data class TimeRoutineUiState(
    val title: String,
    val dayOfWeeks: Set<DayOfWeek>
)
