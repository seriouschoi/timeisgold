package software.seriouschoi.timeisgold.feature.timeroutine.presentation.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import software.seriouschoi.navigator.NavigatorRoute
import software.seriouschoi.timeisgold.core.common.ui.TigTheme
import software.seriouschoi.timeisgold.core.common.ui.TigThemePreview
import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.core.common.ui.asString
import software.seriouschoi.timeisgold.core.common.ui.components.TigAlert
import software.seriouschoi.timeisgold.core.common.ui.components.TigBottomBar
import software.seriouschoi.timeisgold.core.common.ui.components.TigButtonTypes
import software.seriouschoi.timeisgold.core.common.ui.components.TigIconButton
import software.seriouschoi.timeisgold.core.common.ui.components.TigLabelButton
import software.seriouschoi.timeisgold.core.common.ui.components.TigScaffold
import software.seriouschoi.timeisgold.core.common.ui.components.TigSingleLineTextField
import software.seriouschoi.timeisgold.core.common.ui.container.TigBlurContainer
import software.seriouschoi.timeisgold.core.common.ui.dialog.TigTimePickerDialog
import software.seriouschoi.timeisgold.core.common.util.Envelope
import software.seriouschoi.timeisgold.core.common.util.asFormattedString
import java.time.LocalTime
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
    val validUiState by viewModel.validStateFlow.collectAsState()
    UiStateView(
        uiState = uiState,
        uiValidState = validUiState,
        sendIntent = {
            viewModel.sendIntent(it)
        },
    )

    val uiEvent by viewModel.uiEvent.collectAsState(
        initial = null
    )
    UiEventView(uiEvent) {
        viewModel.sendIntent(it)
    }
}

@Composable
private fun UiEventView(
    envelope: Envelope<TimeSlotEditUiEvent>?,
    sendIntent: (TimeSlotEditIntent) -> Unit,
) {
    val event = envelope?.payload
    when (event) {
        is TimeSlotEditUiEvent.SelectTime -> {
            TigTimePickerDialog(
                time = event.time,
                dialogId = envelope.uuid.toString(),
            ) {
                sendIntent(TimeSlotEditIntent.SelectedTime(it, event.isStartTime))
            }
        }

        is TimeSlotEditUiEvent.ShowConfirm -> {
            TigAlert(
                alertId = envelope.uuid.toString(),
                message = event.message.asString(),
                confirmButtonText = stringResource(CommonR.string.text_confirm),
                onClickConfirm = {
                    event.confirmIntent?.let { sendIntent(it) }
                },
                cancelButtonText = stringResource(CommonR.string.text_cancel),
                onClickCancel = {
                    //no work.
                },
            )
        }

        is TimeSlotEditUiEvent.ShowAlert -> {
            TigAlert(
                alertId = envelope.uuid.toString(),
                message = event.message.asString(),
                confirmButtonText = stringResource(CommonR.string.text_confirm),
                onClickConfirm = {
                    event.confirmIntent?.let { sendIntent(it) }
                },
            )
        }

        null -> {
            //no work.
        }

    }
}

@Composable
private fun UiStateView(
    uiState: TimeSlotEditUiState,
    uiValidState: TimeSlotEditValidUiState,
    sendIntent: (TimeSlotEditIntent) -> Unit
) {
    TigBlurContainer(
        enableBlur = uiState.loading,
    ) {
        TigScaffold(
            topBar = {
                UiStateViewTopBar(
                    uiState = uiState,
                    validState = uiValidState,
                    sendIntent = sendIntent,
                )
            },
            content = {
                UiStateViewContentView(
                    uiState = uiState,
                    validState = uiValidState,
                    sendIntent = sendIntent,
                )
            },
            bottomBar = {
                UiStateViewBottomBar(
                    uiState = uiState,
                    validState = uiValidState,
                    sendIntent = sendIntent,
                )
            }
        )
    }
}

@Composable
private fun UiStateViewBottomBar(
    uiState: TimeSlotEditUiState,
    validState: TimeSlotEditValidUiState,
    sendIntent: (TimeSlotEditIntent) -> Unit,
) {
    BottomAppBar {
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
                modifier = Modifier.fillMaxWidth(),
                enabled = validState.enableSaveButton
            ) {
                sendIntent(TimeSlotEditIntent.Save)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UiStateViewContentView(
    uiState: TimeSlotEditUiState,
    validState: TimeSlotEditValidUiState,
    sendIntent: (TimeSlotEditIntent) -> Unit,
) {
    val startTime = uiState.startTime.asFormattedString()
    val endTime = uiState.endTime.asFormattedString()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(
                onClick = {
                    sendIntent(TimeSlotEditIntent.SelectTime(uiState.startTime, true))
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = startTime,
                    style = MaterialTheme.typography.displaySmall
                )
            }
            TextButton(
                onClick = {
                    sendIntent(TimeSlotEditIntent.SelectTime(uiState.endTime, false))
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = endTime,
                    style = MaterialTheme.typography.displaySmall
                )
            }
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        if (validState.invalidMessage != null) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = validState.invalidMessage.asString(),
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UiStateViewTopBar(
    uiState: TimeSlotEditUiState,
    validState: TimeSlotEditValidUiState,
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
private fun Preview() {
    TigTheme {
        UiStateView(
            uiState = TimeSlotEditUiState(
                slotName = "test",
                startTime = LocalTime.of(13, 0),
                endTime = LocalTime.of(14, 30)
            ),
            uiValidState = TimeSlotEditValidUiState(
                enableSaveButton = false,
                invalidMessage = UiText.Res(CommonR.string.message_error_valid_empty_title)
            ),
            sendIntent = {}

        )
    }
}