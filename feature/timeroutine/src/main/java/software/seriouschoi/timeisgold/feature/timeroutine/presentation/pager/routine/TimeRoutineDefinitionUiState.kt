package software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager.routine

import software.seriouschoi.timeisgold.core.common.ui.UiText
import java.time.DayOfWeek

/**
 * Created by jhchoi on 2025. 10. 4.
 * jhchoi
 */
data class TimeRoutineDefinitionUiState(
    val title: UiText = UiText.Raw(""),
    val loading: Boolean = false
)