package software.seriouschoi.timeisgold.feature.timeroutine.edit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import software.seriouschoi.timeisgold.core.common.ui.asString
import software.seriouschoi.timeisgold.core.common.ui.components.TigBottomBar
import software.seriouschoi.timeisgold.core.common.ui.components.TigLabelButton
import software.seriouschoi.timeisgold.core.common.ui.components.TigText
import software.seriouschoi.timeisgold.core.common.ui.components.TigTextField
import software.seriouschoi.timeisgold.core.common.ui.components.TigVerticalCheckBox
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale
import software.seriouschoi.timeisgold.core.common.ui.R as CommonR

@Preview
@Composable
internal fun TimeRoutineEditScreen() {
    val viewModel = hiltViewModel<TimeRoutineEditViewModel>()
    Column {
        Box {
            Loading()
            Edit()
        }
        BottomButtons({
            viewModel.sendIntent(it)
        })

    }

    Alert()
    Confirm()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Confirm() {
    val viewModel = hiltViewModel<TimeRoutineEditViewModel>()
    var showDialog: TimeRoutineEditUiEvent.ShowConfirm? by rememberSaveable { mutableStateOf(null) }
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            if (event is TimeRoutineEditUiEvent.ShowConfirm) {
                showDialog = event
            }
        }
    }

    val currentDialog = showDialog

    if (currentDialog != null) {
        BasicAlertDialog(
            onDismissRequest = {
                showDialog = null
            }
        ) {
            Column {
                Text(text = currentDialog.message.asString())

                Row {
                    Button(
                        onClick = {
                            currentDialog.cancelIntent?.let {
                                viewModel.sendIntent(it)
                            }
                            showDialog = null
                        }
                    ) {
                        Text(text = stringResource(CommonR.string.text_cancel))
                    }
                    Button(
                        onClick = {
                            currentDialog.confirmIntent.let {
                                viewModel.sendIntent(it)
                            }
                            showDialog = null
                        }
                    ) {
                        Text(text = stringResource(CommonR.string.text_confirm))
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Alert() {
    val viewModel = hiltViewModel<TimeRoutineEditViewModel>()
    var showDialog: TimeRoutineEditUiEvent.ShowAlert? by rememberSaveable { mutableStateOf(null) }
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            if (event is TimeRoutineEditUiEvent.ShowAlert) {
                showDialog = event
            }
        }
    }

    val currentDialog = showDialog

    if (currentDialog != null) {
        BasicAlertDialog(
            onDismissRequest = {
                showDialog = null
            }
        ) {
            Column {
                Text(text = currentDialog.message.asString())

                Button(
                    onClick = {
                        currentDialog.confirmIntent?.let {
                            viewModel.sendIntent(it)
                        }
                        showDialog = null
                    }
                ) {
                    Text(text = stringResource(CommonR.string.text_confirm))
                }
            }
        }
    }
}

@Composable
private fun Loading() {
    val viewModel = hiltViewModel<TimeRoutineEditViewModel>()
    val loading by remember(viewModel) {
        viewModel.uiState.map { it: TimeRoutineEditUiState ->
            it is TimeRoutineEditUiState.Loading
        }.distinctUntilChanged()
    }.collectAsState(false)

    if (!loading)
        return

    CircularProgressIndicator()
}

@Composable
private fun Edit() {
    val viewModel = hiltViewModel<TimeRoutineEditViewModel>()

    val currentRoutine by remember(viewModel) {
        viewModel.uiState.map { it: TimeRoutineEditUiState ->
            (it as? TimeRoutineEditUiState.Routine)
        }.distinctUntilChanged()
    }.collectAsState(TimeRoutineEditUiState.Routine())

    currentRoutine ?: return

    Column {
        TigText("timeRoutineEditScreen. ${currentRoutine?.currentDayOfWeek}")
        TigTextField(
            value = currentRoutine?.routineTitle ?: "",
            onValueChange = {
                viewModel.updateRoutineTitle(it)
            }
        )

        Row {
            DayOfWeek.entries.forEach { dayOfWeek ->
                val isChecked = currentRoutine?.dayOfWeekList?.contains(dayOfWeek) == true
                TigVerticalCheckBox(
                    label = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    checked = isChecked,
                    onCheckedChange = {
                        viewModel.checkDayOfWeek(dayOfWeek, it)
                    }
                )
            }
        }
    }
}