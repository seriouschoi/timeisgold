package software.seriouschoi.timeisgold.feature.timeroutine.edit

import software.seriouschoi.timeisgold.core.common.ui.UiText
import java.util.UUID

internal sealed interface TimeRoutineEditUiEvent {
    val uuid: UUID
    data class ShowConfirm(
        val message: UiText,
        val confirmIntent: TimeRoutineEditUiIntent,
        val cancelIntent: TimeRoutineEditUiIntent?,
        override val uuid: UUID = UUID.randomUUID(),
    ) : TimeRoutineEditUiEvent

    data class ShowAlert(
        val message: UiText,
        val confirmIntent: TimeRoutineEditUiIntent?,
        override val uuid: UUID = UUID.randomUUID(),
    ) : TimeRoutineEditUiEvent
}