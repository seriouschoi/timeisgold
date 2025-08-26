package software.seriouschoi.timeisgold.feature.timeroutine.bar.tablayout

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import software.seriouschoi.navigator.NavigatorDest

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
internal fun TimeRoutineTabBarScreen(
    viewModel: TimeRoutineTabBarViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = {7},
        initialPageOffsetFraction = 0f
    )
    HorizontalPager(
        state = pagerState
    ) {

    }
}

