package software.seriouschoi.timeisgold.feature.timeroutine.pager

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import software.seriouschoi.navigator.NavigatorRoute
import software.seriouschoi.timeisgold.core.common.ui.components.InfiniteHorizontalPager
import software.seriouschoi.timeisgold.feature.timeroutine.page.TimeRoutinePageScreen
import software.seriouschoi.timeisgold.feature.timeroutine.page.TimeRoutinePageUiIntent

/**
 * Created by jhchoi on 2025. 8. 26.
 * jhchoi
 */

@Serializable
internal data object TimeRoutinePagerScreenRoute : NavigatorRoute {
    fun routes(navGraphBuilder: NavGraphBuilder) {
        navGraphBuilder.composable<TimeRoutinePagerScreenRoute> {
            TimeRoutinePagerScreen()
        }
    }
}

@Composable
internal fun TimeRoutinePagerScreen() {
    val viewModel: TimeRoutinePagerViewModel = hiltViewModel()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    TimeRoutinePageView(uiState)
}

@Composable
private fun TimeRoutinePageView(
    uiState: TimeRoutinePagerUiState,
) {
    val dayOfWeeks = uiState.dayOfWeekList
    val startIndex = uiState.dayOfWeekList.indexOf(uiState.today)
    InfiniteHorizontalPager(
        pageList = uiState.dayOfWeekList,
        startPageIndex = startIndex
    ) {
        val dayOfWeek = dayOfWeeks[it]
        TimeRoutinePageScreen(
            dayOfWeek = dayOfWeek
        )
    }
}



