package software.seriouschoi.timeisgold.feature.timeroutine.bar.tablayout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
        pageCount = { uiState.dayOfWeekList.size },
        initialPageOffsetFraction = 0f
    )
    HorizontalPager(
        state = pagerState
    ) { page: Int ->
        // TODO: jhchoi 2025. 8. 26. 테스트 뷰.
        Column(modifier = Modifier.fillMaxSize()) {
            Text(text = page.toString())
            Text(text = uiState.dayOfWeekList[page].dayOfWeek.toString())
        }
    }
}

