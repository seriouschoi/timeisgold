package software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import software.seriouschoi.navigator.NavigatorRoute
import software.seriouschoi.timeisgold.core.common.ui.TigTheme
import software.seriouschoi.timeisgold.core.common.ui.TigThemePreview
import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.core.common.ui.asString
import software.seriouschoi.timeisgold.core.common.ui.components.InfiniteHorizontalPager
import software.seriouschoi.timeisgold.core.common.ui.components.TigCircleText
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.dayofweeks.DayOfWeeksView
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager.routine.RoutineTitleView
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.TimeSlotListPageScreen

/**
 * Created by jhchoi on 2025. 8. 26.
 * jhchoi
 */

@Serializable
internal data object TimeRoutinePagerScreenRoute : NavigatorRoute {
    fun routes(navGraphBuilder: NavGraphBuilder) {
        navGraphBuilder.composable<TimeRoutinePagerScreenRoute> {
            Screen()
        }
    }
}

@Composable
private fun Screen() {
    val viewModel: TimeRoutinePagerViewModel = hiltViewModel()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    TimeRoutinePagerRootView(uiState = uiState, sendIntent = {
        viewModel.sendIntent(it)
    })
}

@Composable
private fun TimeRoutinePagerRootView(
    uiState: TimeRoutinePagerUiState,
    sendIntent: (TimeRoutinePagerUiIntent) -> Unit,
) {
    Scaffold(
        contentWindowInsets = WindowInsets(),
        topBar = {
            TopBar(uiState, sendIntent)
        },
        content = { innerPadding ->
            Column(
                Modifier.Companion
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                DayOfWeeksView(modifier = Modifier.fillMaxWidth())
                PagerView(
                    uiState = uiState,
                    sendIntent = sendIntent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        },
        floatingActionButton = {
            if (uiState.showAddTimeSlotButton) {
                FloatingActionButton(
                    onClick = {
                        sendIntent(TimeRoutinePagerUiIntent.AddRoutine)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    uiState: TimeRoutinePagerUiState,
    sendIntent: (TimeRoutinePagerUiIntent) -> Unit,
) {
    TopAppBar(
        title = {
            RoutineTitleView()
        },
        navigationIcon = {
            TigCircleText(text = uiState.dayOfWeekName.asString())
        },
        actions = {
            IconButton(
                onClick = {
                    sendIntent(TimeRoutinePagerUiIntent.ModifyRoutine)
                }
            ) {
                Icon(Icons.Default.Edit, contentDescription = null)
            }
        },
    )
}

@Composable
private fun PagerView(
    uiState: TimeRoutinePagerUiState,
    sendIntent: (TimeRoutinePagerUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerItems = uiState.pagerItems

    InfiniteHorizontalPager(
        pageList = pagerItems,
        initialPageIndex = uiState.initialPageIndex,
        onSelectPage = {
            val dayOfWeek = pagerItems.getOrNull(it)
            if (dayOfWeek != null) {
                sendIntent(TimeRoutinePagerUiIntent.LoadRoutine(dayOfWeek))
            }
        },
        modifier = modifier,
    ) {
        val dayOfWeek = pagerItems[it]
        TimeSlotListPageScreen(
            dayOfWeek = dayOfWeek
        )
    }
}

@TigThemePreview
@Composable
private fun Preview() {
    TigTheme {
        TimeRoutinePagerRootView(
            uiState = TimeRoutinePagerUiState(
                title = UiText.Raw("제목"),
                dayOfWeekName = UiText.Raw("금")
            ),
            sendIntent = {

            })
    }
}



