package software.seriouschoi.timeisgold.core.common.util

import java.util.UUID

data class Envelope<T>(
    val payload: T,
    val uuid: UUID = UUID.randomUUID(),
)