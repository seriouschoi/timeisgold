package software.seriouschoi.timeisgold.core.common.util

import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.round

/**
 * Created by jhchoi on 2025. 8. 28.
 * jhchoi
 */

fun LocalDateTime.toEpochMillis(zoneId: ZoneId = ZoneId.systemDefault()): Long {
    return this.atZone(zoneId).toInstant().toEpochMilli()
}


fun LocalTime.asFormattedString(pattern: String = "HH:mm"): String {
    return this.format(DateTimeFormatter.ofPattern(pattern))
}

fun LocalTime.asMinutes(): Int {
    return this.hour * 60 + this.minute
}

fun LocalTime.normalize(minute: Float = 15f): LocalTime {
    val newMinute = (round(this.minute / minute) * minute).toInt() % 60
    return this.withMinute(newMinute)
}