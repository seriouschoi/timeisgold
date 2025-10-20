package software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager

import software.seriouschoi.timeisgold.feature.timeroutine.presentation.components.dayofweeks.pager.DayOfWeeksPagerState
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.components.dayofweeks.check.DayOfWeeksCheckState
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager.stateholder.RoutineTitleState

internal data class TimeRoutinePagerUiState(
    val dayOfWeekState: DayOfWeeksPagerState = DayOfWeeksPagerState(),
    val titleState: RoutineTitleState = RoutineTitleState(),
    val routineDayOfWeeks: DayOfWeeksCheckState = DayOfWeeksCheckState()
)