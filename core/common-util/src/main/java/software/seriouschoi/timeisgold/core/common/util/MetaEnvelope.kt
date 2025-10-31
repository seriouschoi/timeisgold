package software.seriouschoi.timeisgold.core.common.util

import java.time.Instant
import java.util.UUID

data class MetaEnvelope<T>(
    val payload: T,
    val metaInfo: MetaInfo = MetaInfo.createNew()
)

data class MetaInfo(
    val uuid: String,
    val createTime: Instant
) {
    companion object {
        fun createNew(): MetaInfo {
            return MetaInfo(
                uuid = UUID.randomUUID().toString(),
                createTime = Instant.now()
            )
        }
    }
}