package software.seriouschoi.timeisgold.feature.timeroutine.presentation.edit.slot

import software.seriouschoi.timeisgold.core.common.ui.UiText
import java.time.LocalTime

internal data class TimeSlotEditUiState(
    val slotName: String = "",
    val visibleDelete: Boolean = false,
    val startTime: LocalTime = LocalTime.now(),
    val endTime: LocalTime = LocalTime.now(),
    val loading: Boolean = false
)

internal data class TimeSlotEditValidUiState(
    val enableSaveButton: Boolean = false,
    val invalidMessage: UiText? = null
)
