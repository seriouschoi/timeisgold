package software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager.routine

import software.seriouschoi.timeisgold.core.common.ui.UiText

/**
 * Created by jhchoi on 2025. 10. 4.
 * jhchoi
 */
data class RoutineTitleUiState(
    val title: String = "",
    val loading: Boolean = false,
    val error: UiText? = null
)