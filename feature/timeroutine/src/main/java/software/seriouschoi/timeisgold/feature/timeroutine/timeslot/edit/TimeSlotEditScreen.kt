package software.seriouschoi.timeisgold.feature.timeroutine.timeslot.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import software.seriouschoi.navigator.NavigatorRoute
import software.seriouschoi.timeisgold.core.common.ui.TigTheme
import software.seriouschoi.timeisgold.core.common.ui.TigThemePreview
import software.seriouschoi.timeisgold.core.common.ui.components.TigBottomBar
import software.seriouschoi.timeisgold.core.common.ui.components.TigButtonTypes
import software.seriouschoi.timeisgold.core.common.ui.components.TigIconButton
import software.seriouschoi.timeisgold.core.common.ui.components.TigLabelButton
import software.seriouschoi.timeisgold.core.common.ui.components.TigScaffold
import software.seriouschoi.timeisgold.core.common.ui.components.TigSingleLineTextField
import software.seriouschoi.timeisgold.core.common.ui.dialog.TigTimePickerDialog
import software.seriouschoi.timeisgold.core.common.util.formatToString
import java.time.LocalTime
import java.util.UUID
import software.seriouschoi.timeisgold.core.common.ui.R as CommonR

@Serializable
internal data class TimeSlotEditScreenRoute(
    val timeRoutineUuid: String,
    val timeSlotUuid: String?,
) : NavigatorRoute {
    companion object {
        fun routes(navGraphBuilder: NavGraphBuilder) {
            navGraphBuilder.composable<TimeSlotEditScreenRoute> {
                Screen()
            }
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
            buttonType = TigButtonTypes.Primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            sendIntent(TimeSlotEditIntent.Save)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UiStateViewContentView(
    uiState: TimeSlotEditUiState,
    sendIntent: (TimeSlotEditIntent) -> Unit,
) {
    val startTime = uiState.startTime.formatToString()
    val endTime = uiState.endTime.formatToString()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TextButton(
            onClick = {
                sendIntent(TimeSlotEditIntent.SelectTime(uiState.startTime, true))
            }
        ) {
            Text(
                text = startTime,
                style = MaterialTheme.typography.displaySmall
            )
        }

        TextButton(
            onClick = {
                sendIntent(TimeSlotEditIntent.SelectTime(uiState.endTime, false))
            }
        ) {
            Text(
                text = endTime,
                style = MaterialTheme.typography.displaySmall
            )
        }
    }
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

@Composable
@TigThemePreview
fun Preview() {
    TigTheme {
        UiStateView(
            uiState = TimeSlotEditUiState(
                slotName = "test",
                startTime = LocalTime.of(13, 0),
                endTime = LocalTime.of(14, 30)
            ),
            sendIntent = {}

        )
    }
}