package software.seriouschoi.timeisgold.domain.data.vo

import java.time.DayOfWeek

/**
 * Created by jhchoi on 2025. 10. 28.
 * jhchoi
 */
data class TimeRoutineVO(
    val title: String,
    val dayOfWeeks: Set<DayOfWeek>,
)
