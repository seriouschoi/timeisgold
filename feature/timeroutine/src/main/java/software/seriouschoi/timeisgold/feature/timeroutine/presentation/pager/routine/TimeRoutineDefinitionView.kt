package software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager.routine

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import software.seriouschoi.timeisgold.core.common.ui.TigTheme
import software.seriouschoi.timeisgold.core.common.ui.TigThemePreview
import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.core.common.ui.asString
import software.seriouschoi.timeisgold.core.common.ui.components.TigSingleLineTextField

/**
 * Created by jhchoi on 2025. 10. 4.
 * jhchoi
 */
@Composable
internal fun TimeRoutineDefinitionView() {
    val viewModel = hiltViewModel<TimeRoutineDefinitionViewModel>()

    val uiState by viewModel.uiState.collectAsState()
    StateView(uiState)
}

@Composable
private fun StateView(
    uiState: TimeRoutineDefinitionUiState,
    sendIntent: (TimeRoutineDefinitionIntent) -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TigSingleLineTextField(
            value = uiState.title.asString(),
            onValueChange = {
                sendIntent.invoke(TimeRoutineDefinitionIntent.EditTitle(title = it))
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@TigThemePreview
@Composable
private fun Preview() {
    TigTheme {
        StateView(
            uiState = TimeRoutineDefinitionUiState(
                title = UiText.Raw("Test"),
            )
        )
    }
}