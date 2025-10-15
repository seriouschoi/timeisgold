package software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import software.seriouschoi.navigator.NavigatorRoute
import software.seriouschoi.timeisgold.core.common.ui.TigTheme
import software.seriouschoi.timeisgold.core.common.ui.TigThemePreview
import software.seriouschoi.timeisgold.core.common.ui.asString
import software.seriouschoi.timeisgold.core.common.ui.components.InfiniteHorizontalPager
import software.seriouschoi.timeisgold.core.common.ui.components.TigCheckButton
import software.seriouschoi.timeisgold.core.common.ui.components.TigCircleText
import software.seriouschoi.timeisgold.core.common.ui.components.TigSingleLineTextField
import software.seriouschoi.timeisgold.core.common.util.asShortText
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager.stateholder.RoutineDayOfWeeksState
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager.stateholder.RoutineTitleState
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.TimeSlotListPageScreen
import timber.log.Timber
import software.seriouschoi.timeisgold.core.common.ui.R as CommonR

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
            TopBar(uiState = uiState, sendIntent = sendIntent)
        },
        content = { innerPadding ->
            Column(
                Modifier.Companion
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                TimeRoutineDayOfWeekView(
                    state = uiState.routineDayOfWeeks,
                    sendIntent = sendIntent
                )
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
    )
}

@Composable
private fun TimeRoutineDayOfWeekView(
    state: RoutineDayOfWeeksState,
    sendIntent: (TimeRoutinePagerUiIntent) -> Unit = {}
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        state.dayOfWeeksList.forEach { item ->
            TigCheckButton(
                label = item.displayName.asString(),
                checked = item.checked,
                onCheckedChange = {
                    sendIntent.invoke(
                        TimeRoutinePagerUiIntent.CheckDayOfWeek(
                            dayOfWeek = item.dayOfWeek,
                            checked = it
                        )
                    )
                },
                enabled = item.enabled
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    uiState: TimeRoutinePagerUiState,
    sendIntent: (TimeRoutinePagerUiIntent) -> Unit,
) {
    TopAppBar(
        title = {
            TitleText(uiState.titleState, sendIntent)

        },
        navigationIcon = {
            TigCircleText(text = uiState.dayOfWeekState.currentDayOfWeek.asShortText())
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
private fun TitleText(titleState: RoutineTitleState, sendIntent: (TimeRoutinePagerUiIntent) -> Unit) {
    Timber.d("TitleText - titleState=$titleState")
    TigSingleLineTextField(
        value = titleState.title,
        onValueChange = {
            sendIntent.invoke(TimeRoutinePagerUiIntent.UpdateRoutineTitle(it))
        },
        modifier = Modifier.fillMaxWidth(),
        hint = stringResource(CommonR.string.text_routine_title)
    )
}

@Composable
private fun PagerView(
    uiState: TimeRoutinePagerUiState,
    sendIntent: (TimeRoutinePagerUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerItems = uiState.dayOfWeekState.dayOfWeeks
    val currentDayOfWeek = uiState.dayOfWeekState.currentDayOfWeek

    InfiniteHorizontalPager(
        pageList = pagerItems,
        initialPageIndex = pagerItems.indexOfFirst { it == currentDayOfWeek },
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
            ),
            sendIntent = {

            })
    }
}



