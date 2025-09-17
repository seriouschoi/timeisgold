package software.seriouschoi.timeisgold.feature.timeroutine.timeslot.edit

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import software.seriouschoi.navigator.NavigatorRoute
import software.seriouschoi.timeisgold.core.common.ui.components.TigBottomBar
import software.seriouschoi.timeisgold.core.common.ui.components.TigButtonTypes
import software.seriouschoi.timeisgold.core.common.ui.components.TigIconButton
import software.seriouschoi.timeisgold.core.common.ui.components.TigLabelButton
import software.seriouschoi.timeisgold.core.common.ui.components.TigScaffold
import software.seriouschoi.timeisgold.core.common.ui.components.TigSingleLineTextField
import software.seriouschoi.timeisgold.core.common.ui.R as CommonR

@Serializable
internal data object TimeSlotEditScreenRoute : NavigatorRoute {
    fun routes(navGraphBuilder: NavGraphBuilder) {
        navGraphBuilder.composable<TimeSlotEditScreenRoute> {
            Screen()
        }
    }
}

@Composable
private fun Screen() {
    val viewModel = hiltViewModel<TimeSlotEditViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    UiStateView(uiState) {
        viewModel.sendIntent(it)
    }
}

@Composable
private fun UiStateView(uiState: TimeSlotEditUiState, sendIntent: (TimeSlotEditIntent) -> Unit) {
    TigScaffold(
        topBar = {
            UiStateViewTopBar(
                uiState = uiState,
                sendIntent = sendIntent,
            )
        },
        content = {
            UiStateViewContentView(
                uiState = uiState,
                sendIntent = sendIntent,
            )
        },
        bottomBar = {
            UiStateViewBottomBar(
                uiState = uiState,
                sendIntent = sendIntent,
            )
        }
    )
}

@Composable
private fun UiStateViewBottomBar(
    uiState: TimeSlotEditUiState,
    sendIntent: (TimeSlotEditIntent) -> Unit,
) {
    TigBottomBar {
        if (uiState.visibleDelete) {
            TigLabelButton(
                label = stringResource(CommonR.string.text_delete),
            ) {
                sendIntent(TimeSlotEditIntent.Delete)
            }
        }
        TigLabelButton(
            label = stringResource(CommonR.string.text_save),
            buttonType = TigButtonTypes.Primary
        ) {
            sendIntent(TimeSlotEditIntent.Save)
        }
    }
}

@Composable
private fun UiStateViewContentView(
    uiState: TimeSlotEditUiState,
    sendIntent: (TimeSlotEditIntent) -> Unit,
) {
    /*
    val startTime: LocalTime,
    val endTime: LocalTime,
     */
    // TODO: 시작시간, 종료시간 선택.
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UiStateViewTopBar(
    uiState: TimeSlotEditUiState,
    sendIntent: (TimeSlotEditIntent) -> Unit,
) {
    TopAppBar(
        title = {
            TigSingleLineTextField(
                value = uiState.slotName,
                onValueChange = {
                    sendIntent(TimeSlotEditIntent.UpdateSlotName(it))
                },
            )
        },
        navigationIcon = {
            TigIconButton(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            ) {
                sendIntent(TimeSlotEditIntent.Back)
            }
        }
    )
}