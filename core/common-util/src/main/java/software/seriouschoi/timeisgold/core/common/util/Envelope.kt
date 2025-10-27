package software.seriouschoi.timeisgold.core.common.util

import java.time.OffsetDateTime
import java.util.UUID

data class Envelope<T>(
    val payload: T,
    val uuid: UUID = UUID.randomUUID(),
)

data class MetaEnvelope<T>(
    val payload: T,
    val metaInfo: MetaInfo? = null
)

data class MetaInfo(
    val uuid: String,
    val createTime: OffsetDateTime
) {
    companion object {
        fun createNew() : MetaInfo{
            return MetaInfo(
                uuid = UUID.randomUUID().toString(),
                createTime = OffsetDateTime.now()
            )
        }
    }
}