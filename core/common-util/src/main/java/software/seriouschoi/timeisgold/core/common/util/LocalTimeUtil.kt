package software.seriouschoi.timeisgold.core.common.util

import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

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

fun LocalTime.normalize(stepMinutes: Float = 15f): LocalTime {
    val totalMinutes = this.asMinutes()
    val rounded = ((totalMinutes.toFloat() / stepMinutes).roundToInt() * stepMinutes) % (24 * 60)
    val newHour = rounded / 60
    val newMinute = rounded % 60
    val normalized = LocalTime.of(newHour.toInt(), newMinute.toInt())
    return normalized
}

object LocalTimeUtil {
    const val DAY_MINUTES = 60 * 24
    fun create(
        minutesOfDay: Long,
    ): LocalTime {
        val minutesDelta = when {
            minutesOfDay < 0 -> {
                //-1시
                DAY_MINUTES + minutesOfDay
            }
            minutesOfDay >= DAY_MINUTES -> {
                //25시
                0 + (minutesOfDay - DAY_MINUTES)
            }
            else -> {
                minutesOfDay
            }
        }
        return LocalTime.of(0, 0).plusMinutes(minutesDelta)
    }
}