package software.seriouschoi.timeisgold.feature.timeroutine.pager

import software.seriouschoi.timeisgold.core.common.ui.UiText
import java.time.DayOfWeek

internal data class TimeRoutinePagerUiState(
    val pagerItems: List<DayOfWeek> = listOf(),
    val initialPageIndex: Int = 0,

    val title: UiText = UiText.Raw(""),
    val dayOfWeekName: UiText = UiText.Raw(""),
    val showAddTimeSlotButton: Boolean = false,
)