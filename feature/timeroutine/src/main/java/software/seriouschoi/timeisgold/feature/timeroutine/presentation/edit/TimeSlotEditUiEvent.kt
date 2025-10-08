package software.seriouschoi.timeisgold.feature.timeroutine.presentation.edit

import software.seriouschoi.timeisgold.core.common.ui.UiText
import java.time.LocalTime

sealed interface TimeSlotEditUiEvent {
    data class SelectTime(
        val time: LocalTime,
        val isStartTime: Boolean,
    ) : TimeSlotEditUiEvent

    data class ShowConfirm(
        val message: UiText,
        val confirmIntent: TimeSlotEditIntent? = null,
    ) : TimeSlotEditUiEvent

    data class ShowAlert(
        val message: UiText,
        val confirmIntent: TimeSlotEditIntent? = null
    ) : TimeSlotEditUiEvent

}
