package software.seriouschoi.timeisgold.domain.data.vo

import java.time.LocalTime

/**
 * Created by jhchoi on 2025. 10. 27.
 * jhchoi
 */
data class TimeSlotVO(
    val startTime: LocalTime,
    val endTime: LocalTime,
    val title: String,
)
