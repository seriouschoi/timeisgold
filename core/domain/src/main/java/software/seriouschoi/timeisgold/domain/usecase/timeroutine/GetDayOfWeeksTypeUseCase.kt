package software.seriouschoi.timeisgold.domain.usecase.timeroutine

import software.seriouschoi.timeisgold.domain.data.DayOfWeekType
import java.time.DayOfWeek
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 9. 12.
 * jhchoi
 */
class GetDayOfWeeksTypeUseCase @Inject constructor() {
    fun invoke(dayOfWeeks: Set<DayOfWeek>): DayOfWeekType? {
        return DayOfWeekType.entries.find { entry ->
            entry.dayOfWeeks == dayOfWeeks
        }
    }
}