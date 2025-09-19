package software.seriouschoi.timeisgold.domain.data.entities

import software.seriouschoi.timeisgold.core.common.util.toEpochMillis
import java.time.LocalDateTime
import java.util.UUID

data class TimeRoutineEntity(
    val title: String,
    val uuid: String,
    val createTime: Long,
) {
    companion object {
        fun newEntity(title: String): TimeRoutineEntity {
            return TimeRoutineEntity(
                title = title,
                uuid = UUID.randomUUID().toString(),
                createTime = LocalDateTime.now().toEpochMillis(),
            )
        }
    }
}