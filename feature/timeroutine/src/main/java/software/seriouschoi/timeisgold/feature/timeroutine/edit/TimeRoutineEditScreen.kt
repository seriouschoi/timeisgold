package software.seriouschoi.timeisgold.feature.timeroutine.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import software.seriouschoi.navigator.NavigatorRoute
import software.seriouschoi.timeisgold.core.common.ui.TigTheme
import software.seriouschoi.timeisgold.core.common.ui.TigThemePreview
import software.seriouschoi.timeisgold.core.common.ui.asString
import software.seriouschoi.timeisgold.core.common.ui.components.TigAlert
import software.seriouschoi.timeisgold.core.common.ui.components.TigBottomBar
import software.seriouschoi.timeisgold.core.common.ui.components.TigButtonTypes
import software.seriouschoi.timeisgold.core.common.ui.components.TigCheckButton
import software.seriouschoi.timeisgold.core.common.ui.components.TigLabelButton
import software.seriouschoi.timeisgold.core.common.ui.components.TigScaffold
import software.seriouschoi.timeisgold.core.common.ui.components.TigSingleLineTextField
import software.seriouschoi.timeisgold.core.common.ui.container.TigBlurContainer
import software.seriouschoi.timeisgold.core.common.util.Envelope
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale
import software.seriouschoi.timeisgold.core.common.ui.R as CommonR


@Serializable
internal data class TimeRoutineEditScreenRoute(
    val dayOfWeekOrdinal: Int,
) : NavigatorRoute {
    companion object {
        fun routes(navGraphBuilder: NavGraphBuilder) {
            navGraphBuilder.composable<TimeRoutineEditScreenRoute> { it: NavBackStackEntry ->
                Screen()
            }
        }
    }
}

@Composable
private fun Screen() {
    val viewModel = hiltViewModel<TimeRoutineEditViewModel>()

    //show uiState.
    val uiState by viewModel.uiStateFlow.collectAsState()
    val validState by viewModel.validStateFlow.collectAsState()
    StateView(uiState = uiState, validState = validState, sendIntent = {
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

@TigThemePreview
@Composable
private fun PreviewStateView() {
    TigTheme {
        StateView(
            uiState = TimeRoutineEditUiState(
                currentDayOfWeek = DayOfWeek.MONDAY,
                dayOfWeekMap = TimeRoutineEditDayOfWeekItemState.createDefaultItemMap(),
                routineTitle = "title",
                visibleDelete = true,
                isLoading = false
            ),
            validState = TimeRoutineEditUiValidUiState()
        ) { }
    }
}

@Composable
private fun StateView(
    uiState: TimeRoutineEditUiState,
    validState: TimeRoutineEditUiValidUiState,
    sendIntent: (TimeRoutineEditUiIntent) -> Unit,
) {
    TigBlurContainer(uiState.isLoading) {
        Routine(uiState, validState) {
            sendIntent(it)
        }
    }
}

@Composable
private fun Routine(
    currentRoutine: TimeRoutineEditUiState,
    validState: TimeRoutineEditUiValidUiState,
    sendIntent: (TimeRoutineEditUiIntent) -> Unit,
) {
    TigScaffold(
        topBar = {
            RoutineTopBar(
                currentRoutine,
                validState,
                sendIntent
            )
        },
        content = {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
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
                Text(
                    text = validState.invalidDayOfWeekMessage?.asString() ?: "",
                )
            }
        },
        bottomBar = {
            BottomButtons(currentRoutine, validState, sendIntent)
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoutineTopBar(
    currentRoutine: TimeRoutineEditUiState,
    validState: TimeRoutineEditUiValidUiState,
    sendIntent: (TimeRoutineEditUiIntent) -> Unit
) {
    TopAppBar(
        title = {
            TigSingleLineTextField(
                value = currentRoutine.routineTitle,
                onValueChange = {
                    sendIntent(
                        TimeRoutineEditUiIntent.UpdateRoutineTitle(it)
                    )
                },
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent),
                hint = currentRoutine.subTitle.takeIf { it.isNotEmpty() }
                    ?: stringResource(CommonR.string.text_routine_title)
            )
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    sendIntent(TimeRoutineEditUiIntent.Exit)
                }
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        },
    )
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
    BottomAppBar {
        TigBottomBar {
            if (currentRoutine.visibleDelete) {
                TigLabelButton(
                    label = stringResource(CommonR.string.text_delete),
                    onClick = {
                        sendIntent(TimeRoutineEditUiIntent.Delete)
                    },
                )
            }
            TigLabelButton(
                label = stringResource(CommonR.string.text_save),
                modifier = Modifier.fillMaxWidth(),
                enabled = validState.isValid,
                onClick = {
                    sendIntent(TimeRoutineEditUiIntent.Save)
                },
                buttonType = TigButtonTypes.Primary
            )
        }
    }
}

