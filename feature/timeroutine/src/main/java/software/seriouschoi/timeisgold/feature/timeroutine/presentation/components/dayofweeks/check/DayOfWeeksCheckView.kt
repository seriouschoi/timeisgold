package software.seriouschoi.timeisgold.feature.timeroutine.presentation.components.dayofweeks.check

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import software.seriouschoi.timeisgold.core.common.ui.asString
import software.seriouschoi.timeisgold.core.common.ui.components.TigCheckButton

/**
 * Created by jhchoi on 2025. 10. 16.
 * jhchoi
 */
@Composable
internal fun DayOfWeeksCheckView(
    state: DayOfWeeksCheckState,
    sendIntent: (DayOfWeeksCheckIntent) -> Unit = {}
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        state.dayOfWeeksList.forEach { item ->
            TigCheckButton(
                label = item.displayName.asString(),
                checked = item.checked,
                onCheckedChange = {
                    sendIntent.invoke(
                        DayOfWeeksCheckIntent.Check(
                            dayOfWeek = item.dayOfWeek,
                            checked = it
                        )
                    )
                },
                enabled = item.enabled
            )
        }
    }
}