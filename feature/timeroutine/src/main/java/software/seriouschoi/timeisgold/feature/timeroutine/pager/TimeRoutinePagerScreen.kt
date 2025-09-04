package software.seriouschoi.timeisgold.feature.timeroutine.pager

import androidx.compose.foundation.layout.fillMaxSize
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
import software.seriouschoi.navigator.NavigatorRoute
import software.seriouschoi.timeisgold.core.common.ui.components.InfiniteHorizontalPager
import software.seriouschoi.timeisgold.feature.timeroutine.page.TimeRoutinePageScreen

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

    val dayOfWeekListFlow = remember(viewModel) {
        viewModel.uiState.map { it.dayOfWeekList }.distinctUntilChanged()
    }
    val dayOfWeekList by dayOfWeekListFlow.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )

    //페이저 순환.
    InfiniteHorizontalPager(
        dayOfWeekList
    ) {
        TimeRoutinePageScreen(
            modifier = Modifier.fillMaxSize(),
            dayOfWeek = dayOfWeekList[it]
        )
    }
}



