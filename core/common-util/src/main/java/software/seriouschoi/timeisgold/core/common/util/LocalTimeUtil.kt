package software.seriouschoi.timeisgold.core.common.util

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt


fun LocalTime.asFormattedString(pattern: String = "HH:mm"): String {
    return this.format(DateTimeFormatter.ofPattern(pattern))
}

fun LocalTime.asMinutes(): Int {
    return this.hour * 60 + this.minute
}

fun LocalTime.normalize(stepMinutes: Int = 1): LocalTime {
    val totalMinutes = this.asMinutes()
    return LocalTimeUtil.create(
        totalMinutes, stepMinutes
    )
}

object MinuteOfDayUtil {
    /**
     * @param existingSlots 현재 시간 슬롯 목록
     * @param startHourOfDay 시작 시간 (hour)
     * @return 시작 시간과 종료 시간 Pair
     */
    fun findAvailableRange(
        sortedSlotList: List<Pair<Int, Int>>,
        startHourOfDay: Int,
    ): Pair<Int, Int>? {

        val range = hourRange(startHourOfDay)
        val result = findGap(sortedSlotList, range) ?: return null

        return result.first to result.second
    }

    fun hourRange(startHour: Int): IntRange {
        val start = startHour * 60
        val end = start + 60
        return start..end
    }

    fun findGap(
        sortedSlots: List<Pair<Int, Int>>,
        range: IntRange
    ): Pair<Int, Int>? {
        var cursor = range.first

        for ((slotStart, slotEnd) in sortedSlots) {
            if (cursor >= slotEnd) continue

            if (cursor < slotStart) {
                val gapEnd = minOf(slotStart, range.last)
                if (cursor < gapEnd)
                    return cursor to gapEnd
            }
            cursor = maxOf(cursor, slotEnd)
        }

        return if (cursor < range.last) cursor to range.last else null
    }

    fun findStartTimeRange(
        sortedSlots: List<Pair<Int, Int>>,
        minutesOfDay: Int
    ): Pair<Int, Int> {

        // availableTimeRange의 시작 시간 바로 이전에 끝나는 슬롯을 찾습니다.
        val previousSlot = sortedSlots.lastOrNull { it.second <= minutesOfDay }

        // 이전 슬롯이 있으면 그 슬롯의 종료 시간이 시작 범위의 시작이 됩니다.
        // 이전 슬롯이 없으면, 하루의 시작(00:00)이 시작 범위의 시작이 됩니다.
        val startTimeRangeStart = previousSlot?.second ?: LocalTime.MIN.asMinutes()

        return startTimeRangeStart to minutesOfDay
    }

    fun findEndTimeRange(
        sortedSlots: List<Pair<Int, Int>>,
        minutesOfDay: Int
    ): Pair<Int, Int> {

        // availableTimeRange의 시작 시간 바로 이전에 끝나는 슬롯을 찾습니다.
        val nextSlot = sortedSlots.firstOrNull { it.first >= minutesOfDay }
            ?: sortedSlots.firstOrNull {
                it.first >= minutesOfDay - LocalTimeUtil.DAY_MINUTES
            }

        // 이전 슬롯이 있으면 그 슬롯의 종료 시간이 시작 범위의 시작이 됩니다.
        // 이전 슬롯이 없으면, 하루의 시작(00:00)이 시작 범위의 시작이 됩니다.
        val startTimeRangeStart = nextSlot?.first ?: LocalTime.MAX.asMinutes()

        return minutesOfDay to startTimeRangeStart
    }

    fun sortAndSplitOvernightList(list: List<Pair<Int, Int>>): List<Pair<Int, Int>> {
        return list.map {
            LocalTimeUtil.splitWhenOverMidnight(it.first, it.second)
        }.flatten().sortedBy { it.first }
    }
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

    fun splitWhenOverMidnight(
        startTime: Int,
        endTime: Int,
    ): List<Pair<Int, Int>> {
        return if (startTime > endTime) {
            listOf(
                startTime to DAY_MINUTES,
                0 to endTime
            )
        } else {
            listOf(startTime to endTime)
        }
    }

    fun overlab(
        timeRange1: Pair<LocalTime, LocalTime>,
        timeRange2: Pair<LocalTime, LocalTime>,
    ): Boolean {
        val ranges1 =
            splitWhenOverMidnight(timeRange1.first.asMinutes(), timeRange1.second.asMinutes()).map {
                it.first until it.second
            }
        val ranges2 =
            splitWhenOverMidnight(timeRange2.first.asMinutes(), timeRange2.second.asMinutes()).map {
                it.first until it.second
            }
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

