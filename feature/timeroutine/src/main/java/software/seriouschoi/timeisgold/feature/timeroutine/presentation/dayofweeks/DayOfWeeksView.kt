package software.seriouschoi.timeisgold.feature.timeroutine.presentation.dayofweeks

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import software.seriouschoi.timeisgold.core.common.ui.asString
import software.seriouschoi.timeisgold.core.common.ui.components.TigCheckButton

/**
 * Created by jhchoi on 2025. 10. 12.
 * jhchoi
 */
@Composable
internal fun DayOfWeeksView(modifier: Modifier) {
    val viewModel = hiltViewModel<DayOfWeeksViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    StateView(uiState) {
        viewModel.sendIntent(it)
    }
}

@Composable
private fun StateView(
    state: DayOfWeeksUiState,
    sendIntent: (DayOfWeeksIntent) -> Unit = {}
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        state.dayOfWeekList.forEach { item: DayOfWeekItemUiState ->
            TigCheckButton(
                label = item.displayName.asString(),
                checked = item.checked,
                onCheckedChange = {
                    sendIntent.invoke(
                        DayOfWeeksIntent.EditDayOfWeek(
                            dayOfWeek = item.id,
                            checked = it
                        )
                    )
                },
                enabled = item.enabled
            )
        }
    }
}