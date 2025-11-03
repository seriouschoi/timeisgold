package software.seriouschoi.timeisgold.feature.timeroutine.presentation.components.dayofweeks.pager

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import software.seriouschoi.timeisgold.core.common.ui.components.InfiniteHorizontalPager
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.TimeSlotListPageScreen

/**
 * Created by jhchoi on 2025. 10. 16.
 * jhchoi
 */
@Composable
internal fun DayOfWeekPagerView(
    state: DayOfWeeksPagerState,
    modifier: Modifier = Modifier,
    sendIntent: (DayOfWeeksPagerStateIntent) -> Unit,
) {
    val pagerItems = state.dayOfWeeks
    val currentDayOfWeek = state.currentDayOfWeek

    InfiniteHorizontalPager(
        pageList = pagerItems,
        initialPageIndex = pagerItems.indexOfFirst { it == currentDayOfWeek },
        onSelectPage = {
            val dayOfWeek = pagerItems.getOrNull(it)
            if (dayOfWeek != null) {
                sendIntent.invoke(
                    DayOfWeeksPagerStateIntent.Select(dayOfWeek)
                )
            }
        },
        modifier = modifier,
    ) {
        val dayOfWeek = pagerItems[it]
        TimeSlotListPageScreen(
            dayOfWeek = dayOfWeek,
            isCurrentPage = dayOfWeek == currentDayOfWeek
        )
    }
}