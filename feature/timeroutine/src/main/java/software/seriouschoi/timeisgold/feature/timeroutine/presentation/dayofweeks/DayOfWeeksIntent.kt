package software.seriouschoi.timeisgold.feature.timeroutine.presentation.dayofweeks

import java.time.DayOfWeek

internal sealed interface DayOfWeeksIntent {
    data class EditDayOfWeek(val dayOfWeek: DayOfWeek, val checked: Boolean) : DayOfWeeksIntent
}