package software.seriouschoi.timeisgold.domain.data.entities

import software.seriouschoi.timeisgold.core.common.util.toEpochMillis
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

data class TimeSlotEntity(
    val uuid: String,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val title: String,
    val createTime: Long
) {
    companion object {
        fun create(
            title: String,
        ): TimeSlotEntity {
            return TimeSlotEntity(
                title = title,
                uuid = UUID.randomUUID().toString(),
                startTime = LocalTime.now(),
                endTime = LocalTime.now(),
                createTime = LocalDateTime.now().toEpochMillis()
            )
        }
    }
}
