package software.seriouschoi.timeisgold.data.mapper

import software.seriouschoi.timeisgold.domain.data.entities.TimeRoutineDayOfWeekEntity
import java.time.DayOfWeek

/**
 * Created by jhchoi on 2025. 8. 28.
 * jhchoi
 */
internal fun DayOfWeek.toTimeRoutineDayOfWeekEntity(): TimeRoutineDayOfWeekEntity {
    return TimeRoutineDayOfWeekEntity(
        dayOfWeek = this
    )
}