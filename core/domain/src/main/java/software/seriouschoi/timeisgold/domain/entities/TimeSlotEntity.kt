package software.seriouschoi.timeisgold.domain.entities

import java.time.LocalTime

data class TimeSlotEntity(
    val uuid: String,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val title: String,
    val createTime: Long
)
