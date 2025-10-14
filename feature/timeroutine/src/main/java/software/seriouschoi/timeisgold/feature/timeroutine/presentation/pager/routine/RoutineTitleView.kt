package software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager.routine

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import software.seriouschoi.timeisgold.core.common.ui.TigTheme
import software.seriouschoi.timeisgold.core.common.ui.TigThemePreview
import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.core.common.ui.asString
import software.seriouschoi.timeisgold.core.common.ui.components.TigSingleLineTextField
import software.seriouschoi.timeisgold.core.common.ui.R as CommonR

/**
 * Created by jhchoi on 2025. 10. 4.
 * jhchoi
 */
@Deprecated("뷰모델은 스크린 단위로만 권장. 이벤트 처리가 필요한 상황이 반드시 오기 때문.")
@Composable
internal fun RoutineTitleView() {
    val viewModel = hiltViewModel<RoutineTitleViewModel>()

    val uiState by viewModel.uiState.collectAsState()
    StateView(uiState) {
        viewModel.sendIntent(it)
    }
}

@Composable
private fun StateView(
    uiState: RoutineTitleUiState,
    sendIntent: (RoutineTitleIntent) -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TigSingleLineTextField(
            value = uiState.title,
            onValueChange = {
                sendIntent.invoke(RoutineTitleIntent.EditTitle(title = it))
            },
            modifier = Modifier.fillMaxWidth(),
            hint = stringResource(CommonR.string.text_routine_title)
        )
        if(uiState.error != null) {
            Text(
                text = uiState.error.asString(LocalContext.current),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }
    }
}

@TigThemePreview
@Composable
private fun Preview() {
    TigTheme {
        StateView(
            uiState = RoutineTitleUiState(
                title = "Test",
                loading = false,
                error = UiText.Raw("error")
            )
        )
    }
}