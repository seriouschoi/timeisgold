package software.seriouschoi.software.seriouschoi.util.localtime

import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Created by jhchoi on 2025. 8. 28.
 * jhchoi
 */

fun LocalDateTime.toEpochMillis(zoneId: ZoneId = ZoneId.systemDefault()): Long {
    return this.atZone(zoneId).toInstant().toEpochMilli()
}