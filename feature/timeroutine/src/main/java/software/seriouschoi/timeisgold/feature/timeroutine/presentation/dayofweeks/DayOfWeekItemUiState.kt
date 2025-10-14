package software.seriouschoi.timeisgold.feature.timeroutine.presentation.dayofweeks

import software.seriouschoi.timeisgold.core.common.ui.UiText
import java.time.DayOfWeek

internal data class DayOfWeekItemUiState(
    val displayName: UiText,
    val enabled: Boolean,
    val checked: Boolean,
    val dayOfWeek: DayOfWeek
)