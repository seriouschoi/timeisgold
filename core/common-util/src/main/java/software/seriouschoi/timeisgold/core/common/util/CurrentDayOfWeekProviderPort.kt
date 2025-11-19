package software.seriouschoi.timeisgold.core.common.util

import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Created by jhchoi on 2025. 11. 19.
 * jhchoi
 */
interface CurrentDayOfWeekProviderPort {
    fun getCurrentDayOfWeek(): DayOfWeek
}

class SystemCurrentDayOfWeeksProviderAdapter : CurrentDayOfWeekProviderPort {
    override fun getCurrentDayOfWeek(): DayOfWeek {
        return LocalDate.now().dayOfWeek
    }
}