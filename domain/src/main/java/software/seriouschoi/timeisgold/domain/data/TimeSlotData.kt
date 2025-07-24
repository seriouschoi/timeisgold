package software.seriouschoi.timeisgold.domain.data

import java.time.LocalTime

data class TimeSlotData(
    val uuid: String,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val title: String,
    val memo: String
)
