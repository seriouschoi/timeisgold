package software.seriouschoi.timeisgold.feature.timeroutine.edit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import software.seriouschoi.timeisgold.core.common.ui.asString
import software.seriouschoi.timeisgold.core.common.ui.R as CommonR

@Composable
internal fun TimeRoutineEditScreen() {

    val viewModel = hiltViewModel<TimeRoutineEditViewModel>()
    Column {
        Box {
            Loading()
            Edit()
        }
        Row {
            Button(
                onClick = {
                    viewModel.sendIntent(TimeRoutineEditUiIntent.Save)
                },
            ) {
                Text(text = stringResource(CommonR.string.text_save))
            }
        }
    }

    Alert()
    Confirm()
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
        Text("timeRoutineEditScreen. ${currentRoutine?.currentDayOfWeek}")
        TextField(
            value = currentRoutine?.routineTitle ?: "",
            onValueChange = {
                viewModel.updateRoutineTitle(it)
            }
        )
    }
}