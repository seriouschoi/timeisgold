package software.seriouschoi.timeisgold.domain.data

import java.time.DayOfWeek

/**
 * Created by jhchoi on 2025. 9. 12.
 * jhchoi
 */
enum class DayOfWeekType(
    val dayOfWeeks: Set<DayOfWeek>
) {
    WeekEnd(
        setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
    ), //주말
    WeekDay(
        setOf(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY
        )
    ), //평일
    EveryDay(
        setOf(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY,
            DayOfWeek.SUNDAY
        )
    )
}