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

    fun sortAndSplitOvernightList(list: List<Pair<Int, Int>>): List<Pair<Int, Int>> {
        return list.map {
            splitWhenOverMidnight(it.first, it.second)
        }.flatten().sortedBy { it.first }
    }

    fun findNextTime(
        sortedList: List<Pair<Int, Int>>,
        minutesOfDay: Int
    ): Pair<Int, Int>? {
        // availableTimeRange의 시작 시간 바로 이전에 끝나는 슬롯을 찾습니다.
        val nextSlot = sortedList.firstOrNull { it.first >= minutesOfDay }
            ?: sortedList.firstOrNull {
                it.first >= minutesOfDay - DAY_MINUTES
            }
        return nextSlot
    }

    fun findPreviousTime(
        sortedList: List<Pair<Int, Int>>,
        minutesOfDay: Int
    ): Pair<Int, Int>? {
        val previousSlot = sortedList.lastOrNull { it.second <= minutesOfDay }
            ?: sortedList.lastOrNull {
                it.second <= DAY_MINUTES + minutesOfDay
            }
        return previousSlot
    }


    /**
     * @param sortedSlotList 현재 시간 슬롯 목록
     * @param startHourOfDay 시작 시간 (hour)
     * @return 시작 시간과 종료 시간 Pair
     */
    fun findAvailableRange(
        sortedSlotList: List<Pair<Int, Int>>,
        startHourOfDay: Int,
    ): Pair<Int, Int>? {

        val range = hourRange(startHourOfDay)
        val result = findGap(sortedSlotList, range.first to range.last) ?: return null

        return result.first to result.second
    }

    fun hourRange(startHour: Int): IntRange {
        val start = startHour * 60
        val end = start + 60
        return start..end
    }

    fun findGap(
        sortedSlots: List<Pair<Int, Int>>,
        range: Pair<Int, Int>
    ): Pair<Int, Int>? {
        var current = range.first

        for ((slotStart, slotEnd) in sortedSlots) {
            // 이 슬롯이 range 밖이면 건너뛴다
            if (slotEnd <= current) continue
            if (range.second <= slotStart) break

            // current ~ slotStart 사이에 gap이 있는지 검사
            val gapEnd = minOf(slotStart, range.second)
            if (current < gapEnd) {
                return current to gapEnd
            }

            // 슬롯이 current를 덮고 있으니 current를 슬롯 끝으로 이동
            current = maxOf(current, slotEnd)
        }

        // 마지막 슬롯 이후에도 range 안에 여유가 있으면 gap
        return if (current < range.second) current to range.second else null
    }
}

