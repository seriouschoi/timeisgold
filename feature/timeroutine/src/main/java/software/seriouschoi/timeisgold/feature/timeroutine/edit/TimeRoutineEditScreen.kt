package software.seriouschoi.timeisgold.feature.timeroutine.edit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import software.seriouschoi.timeisgold.core.common.ui.asString
import software.seriouschoi.timeisgold.core.common.ui.components.TigAlert
import software.seriouschoi.timeisgold.core.common.ui.components.TigBottomBar
import software.seriouschoi.timeisgold.core.common.ui.components.TigCheckButton
import software.seriouschoi.timeisgold.core.common.ui.components.TigCircleProgress
import software.seriouschoi.timeisgold.core.common.ui.components.TigLabelButton
import software.seriouschoi.timeisgold.core.common.ui.components.TigText
import software.seriouschoi.timeisgold.core.common.ui.components.TigTextField
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale
import software.seriouschoi.timeisgold.core.common.ui.R as CommonR

@Composable
internal fun TimeRoutineEditScreen() {
    val viewModel = hiltViewModel<TimeRoutineEditViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    Column {
        Box {
            when (val state = uiState) {
                is TimeRoutineEditUiState.Routine -> {
                    Routine(state) {
                        viewModel.sendIntent(it)
                    }
                }

                TimeRoutineEditUiState.Loading -> {
                    Loading()
                }
            }
        }
        BottomButtons({
            viewModel.sendIntent(it)
        })
    }

    val uiEvent by viewModel.uiEvent.collectAsState(
        initial = null
    )
    ShowEvent(uiEvent) {
        viewModel.sendIntent(it)
    }
}

@Composable
private fun Routine(
    currentRoutine: TimeRoutineEditUiState.Routine,
    sendIntent: (TimeRoutineEditUiIntent) -> Unit
) {
    Column {
        TigText("timeRoutineEditScreen. ${currentRoutine.currentDayOfWeek}")
        TigTextField(
            value = currentRoutine.routineTitle,
            onValueChange = {
                sendIntent(
                    TimeRoutineEditUiIntent.UpdateRoutineTitle(it)
                )
            }
        )

        Row {
            DayOfWeek.entries.forEach { dayOfWeek ->
                TigCheckButton(
                    label = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    checked = currentRoutine.dayOfWeekList.contains(dayOfWeek),
                    onCheckedChange = {
                        sendIntent(
                            TimeRoutineEditUiIntent.UpdateDayOfWeek(dayOfWeek, it)
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun ShowEvent(
    uiEvent: TimeRoutineEditUiEvent?,
    sendIntent: (TimeRoutineEditUiIntent) -> Unit
) {
    when (val currentEvent = uiEvent) {
        is TimeRoutineEditUiEvent.ShowAlert -> {
            TigAlert(
                alertId = currentEvent.uuid.toString(),
                message = currentEvent.message.asString(),
                confirmButtonText = stringResource(CommonR.string.text_confirm),
                onClickConfirm = {
                    currentEvent.confirmIntent?.let { sendIntent(it) }
                }
            )
        }

        is TimeRoutineEditUiEvent.ShowConfirm -> {
            TigAlert(
                alertId = currentEvent.uuid.toString(),
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
    sendIntent: (TimeRoutineEditUiIntent) -> Unit
) {
    TigBottomBar {
        TigLabelButton(
            onClick = {
                sendIntent(TimeRoutineEditUiIntent.Save)
            },
            label = stringResource(CommonR.string.text_save)
        )
    }
}


@Composable
private fun Loading() {
    Box(modifier = Modifier.fillMaxSize()) {
        TigCircleProgress()
    }
}