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

fun LocalTime.normalize(stepMinutes: Int = 1): LocalTime {
    val totalMinutes = this.asMinutes()
    val rounded = LocalTimeUtil.normalize(totalMinutes, stepMinutes)
    val newHour = rounded / 60
    val newMinute = rounded % 60
    val normalized = LocalTime.of(newHour, newMinute)
    return normalized
}

object LocalTimeUtil {
    const val DAY_MINUTES = 60 * 24
    fun create(
        minutesOfDay: Int, stepMinutes: Int = 1
    ): LocalTime {
        val rounded = normalize(minutesOfDay, stepMinutes) % DAY_MINUTES
        return LocalTime.of(0, 0).plusMinutes(rounded.toLong())
    }
    fun normalize(
        minutesOfDay: Int, stepMinutes: Int = 1
    ): Int {
        val rounded = ((minutesOfDay.toFloat() / stepMinutes).roundToInt() * stepMinutes)
        return rounded
    }

    fun splitOverMidnight(
        startTime: LocalTime,
        endTime: LocalTime,
    ): List<IntRange> {
        return if (startTime > endTime) {
            listOf(
                startTime.asMinutes() until DAY_MINUTES,
                0 until endTime.asMinutes()
            )
        } else {
            listOf(startTime.asMinutes() until endTime.asMinutes())
        }
    }

    fun overlab(
        timeRange1: Pair<LocalTime, LocalTime>,
        timeRange2: Pair<LocalTime, LocalTime>,
    ): Boolean {
        val ranges1 = splitOverMidnight(timeRange1.first, timeRange1.second)
        val ranges2 = splitOverMidnight(timeRange2.first, timeRange2.second)
        return ranges1.any { range1 ->
            ranges2.any { range2 ->
                overlab(range1, range2)
            }
        }
    }

    fun overlab(
        range1: IntRange,
        range2: IntRange,
    ): Boolean {
        return range1.first in range2 || range1.last in range2
                || range2.first in range1 || range2.last in range1
    }
}