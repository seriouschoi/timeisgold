package software.seriouschoi.timeisgold.feature.timeroutine.presentation.edit

import software.seriouschoi.timeisgold.core.common.ui.UiText

internal sealed interface TimeRoutineEditUiEvent {
    data class ShowConfirm(
        val message: UiText,
        val confirmIntent: TimeRoutineEditUiIntent,
        val cancelIntent: TimeRoutineEditUiIntent?,
    ) : TimeRoutineEditUiEvent

    data class ShowAlert(
        val message: UiText,
        val confirmIntent: TimeRoutineEditUiIntent?,
    ) : TimeRoutineEditUiEvent
}