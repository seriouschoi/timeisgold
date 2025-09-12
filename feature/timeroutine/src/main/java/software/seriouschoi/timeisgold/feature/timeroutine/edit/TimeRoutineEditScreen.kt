package software.seriouschoi.timeisgold.feature.timeroutine.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import software.seriouschoi.timeisgold.core.common.ui.asString
import software.seriouschoi.timeisgold.core.common.ui.components.TigAlert
import software.seriouschoi.timeisgold.core.common.ui.components.TigBottomBar
import software.seriouschoi.timeisgold.core.common.ui.components.TigCheckButton
import software.seriouschoi.timeisgold.core.common.ui.components.TigLabelButton
import software.seriouschoi.timeisgold.core.common.ui.components.TigSingleLineTextField
import software.seriouschoi.timeisgold.core.common.ui.components.TigText
import software.seriouschoi.timeisgold.core.common.ui.container.TigContainer
import software.seriouschoi.timeisgold.core.common.util.Envelope
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale
import software.seriouschoi.timeisgold.core.common.ui.R as CommonR

@Composable
internal fun TimeRoutineEditScreen() {
    val viewModel = hiltViewModel<TimeRoutineEditViewModel>()

    LaunchedEffect(viewModel) {
        viewModel.init()
    }

    //show uiState.
    val uiState by viewModel.uiStateFlow.collectAsState()
    val validState by viewModel.validStateFlow.collectAsState()
    Screen(uiState = uiState, validState = validState, sendIntent = {
        viewModel.sendIntent(it)
    })

    //show uiEvent
    val uiEvent by viewModel.uiEvent.collectAsState(
        initial = null
    )
    ShowEvent(uiEvent = uiEvent, sendIntent = {
        viewModel.sendIntent(it)
    })
}

@Preview
@Composable
private fun Preview() {
    Screen(
        uiState = TimeRoutineEditUiState(
            currentDayOfWeek = DayOfWeek.MONDAY,
            dayOfWeekMap = TimeRoutineEditDayOfWeekItemState.createDefaultItemMap(),
            routineTitle = "title",
            visibleDelete = true
        ),
        validState = TimeRoutineEditUiValidUiState()
    ) { }
}

@Composable
private fun Screen(
    uiState: TimeRoutineEditUiState,
    validState: TimeRoutineEditUiValidUiState,
    sendIntent: (TimeRoutineEditUiIntent) -> Unit,
) {
    TigContainer(uiState.isLoading) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box {
                Routine(uiState, validState) {
                    sendIntent(it)
                }
            }
        }
    }
}

@Composable
private fun Routine(
    currentRoutine: TimeRoutineEditUiState,
    validState: TimeRoutineEditUiValidUiState,
    sendIntent: (TimeRoutineEditUiIntent) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            TigSingleLineTextField(
                value = currentRoutine.routineTitle,
                onValueChange = {
                    sendIntent(
                        TimeRoutineEditUiIntent.UpdateRoutineTitle(it)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                hint = currentRoutine.subTitle.takeIf { it.isNotEmpty() }
                    ?: stringResource(CommonR.string.text_routine_title)
            )

            TigText(
                text = validState.invalidTitleMessage?.asString() ?: "",
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                currentRoutine.dayOfWeekMap.forEach { dayOfWeekItem ->
                    TigCheckButton(
                        label = dayOfWeekItem.key.getDisplayName(
                            TextStyle.SHORT,
                            Locale.getDefault()
                        ),
                        checked = dayOfWeekItem.value.checked,
                        onCheckedChange = {
                            sendIntent(
                                TimeRoutineEditUiIntent.UpdateDayOfWeek(dayOfWeekItem.key, it)
                            )
                        },
                        enabled = dayOfWeekItem.value.enable
                    )
                }
            }
            TigText(
                text = validState.invalidDayOfWeekMessage?.asString() ?: "",
            )

            BottomButtons(currentRoutine, validState, sendIntent)
        }
    }
}

@Composable
private fun ShowEvent(
    uiEvent: Envelope<TimeRoutineEditUiEvent>?,
    sendIntent: (TimeRoutineEditUiIntent) -> Unit,
) {
    when (val currentEvent = uiEvent?.payload) {
        is TimeRoutineEditUiEvent.ShowAlert -> {
            TigAlert(
                alertId = uiEvent.uuid.toString(),
                message = currentEvent.message.asString(),
                confirmButtonText = stringResource(CommonR.string.text_confirm),
                onClickConfirm = {
                    currentEvent.confirmIntent?.let { sendIntent(it) }
                }
            )
        }

        is TimeRoutineEditUiEvent.ShowConfirm -> {
            TigAlert(
                alertId = uiEvent.uuid.toString(),
                message = currentEvent.message.asString(),
                confirmButtonText = stringResource(CommonR.string.text_confirm),
                onClickConfirm = {
                    sendIntent(currentEvent.confirmIntent)
                },
                cancelButtonText = stringResource(CommonR.string.text_cancel),
                onClickCancel = {
                    currentEvent.cancelIntent?.let { sendIntent(it) }
                }
            )
        }

        null -> {
            //no working.
        }
    }
}

@Composable
private fun BottomButtons(
    currentRoutine: TimeRoutineEditUiState,
    validState: TimeRoutineEditUiValidUiState,
    sendIntent: (TimeRoutineEditUiIntent) -> Unit,
) {
    TigBottomBar {
        if (currentRoutine.visibleDelete) {
            TigLabelButton(
                label = stringResource(CommonR.string.text_delete),
                onClick = {
                    sendIntent(TimeRoutineEditUiIntent.Delete)
                }
            )
        }
        TigLabelButton(
            onClick = {
                sendIntent(TimeRoutineEditUiIntent.Save)
            },
            label = stringResource(CommonR.string.text_save),
            enabled = validState.isValid
        )
    }
}

