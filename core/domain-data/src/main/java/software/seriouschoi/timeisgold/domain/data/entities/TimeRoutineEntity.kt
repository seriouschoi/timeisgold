package software.seriouschoi.timeisgold.domain.data.entities

import software.seriouschoi.software.seriouschoi.util.localtime.toEpochMillis
import java.time.LocalDateTime
import java.util.UUID

data class TimeRoutineEntity(
    val title: String,
    val uuid: String,
    val createTime: Long,
) {
    companion object {
        fun create(title: String): TimeRoutineEntity {
            return TimeRoutineEntity(
                title = title,
                uuid = UUID.randomUUID().toString(),
                createTime = LocalDateTime.now().toEpochMillis(),
            )
        }
    }
}