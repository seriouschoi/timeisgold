package software.seriouschoi.timeisgold.feature.timeroutine.presentation.components.dayofweeks.check

import java.time.DayOfWeek

internal data class DayOfWeeksCheckState(
    val dayOfWeeksList: List<DayOfWeekItemUiState> = emptyList()
)

internal fun DayOfWeeksCheckState.getActiveCheckDayOfWeeks(): List<DayOfWeek> {
    return this.dayOfWeeksList.filter {
        it.enabled && it.checked
    }.map { it.dayOfWeek }
}

internal fun DayOfWeeksCheckState.getActiveDayOfWeeks(): List<DayOfWeek> {
    return this.dayOfWeeksList.filter {
        it.enabled
    }.map { it.dayOfWeek }
}