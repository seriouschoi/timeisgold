package software.seriouschoi.timeisgold.feature.timeroutine.bar.tablayout

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import software.seriouschoi.navigator.NavigatorDest
import software.seriouschoi.timeisgold.feature.timeroutine.bar.timeroutine.TimeRoutineLayout

/**
 * Created by jhchoi on 2025. 8. 26.
 * jhchoi
 */

@Serializable
internal data object TimeRoutineTabBarScreenDest : NavigatorDest

internal fun NavGraphBuilder.tabBar() {
    composable<TimeRoutineTabBarScreenDest> {
        TimeRoutineTabBarScreen()
    }
}

@Composable
internal fun TimeRoutineTabBarScreen() {
    val viewModel: TimeRoutineTabBarViewModel = hiltViewModel()

    val dayOfWeekListFlow = remember(viewModel) {
        viewModel.uiState.map { it.dayOfWeekList }.distinctUntilChanged()
    }
    val dayOfWeekList by dayOfWeekListFlow.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )
    if (dayOfWeekList.isEmpty()) {
        return
    }

    //페이저 순환.
    val pageCount = Int.MAX_VALUE
    val startPage = remember(dayOfWeekList) {
        val size = dayOfWeekList.size
        if (size == 0) 0
        else pageCount / 2 - (pageCount / 2) % size
    }

    val pagerState = rememberPagerState(
        initialPage = startPage,
        pageCount = { pageCount }, //무한.
        initialPageOffsetFraction = 0f
    )
    HorizontalPager(
        state = pagerState
    ) { page: Int ->
        val pageIndex = page % dayOfWeekList.size
        TimeRoutineLayout(
            modifier = Modifier.fillMaxSize(),
            dayOfWeek = dayOfWeekList[pageIndex]
        )
    }
}

