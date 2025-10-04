package software.seriouschoi.timeisgold.feature.timeroutine.page

import software.seriouschoi.timeisgold.core.common.util.LocalTimeUtil
import software.seriouschoi.timeisgold.core.common.util.asFormattedString

internal data class TimeSlotItemUiState(
    val slotUuid: String,
    val routineUuid: String,
    val title: String,
    val startMinutesOfDay: Int,
    val endMinutesOfDay: Int,
    val isSelected: Boolean,
)

internal fun TimeSlotItemUiState.getStartTimeText(): String {
    return LocalTimeUtil.create(startMinutesOfDay).asFormattedString()
}

internal fun TimeSlotItemUiState.getEndTimeText() : String {
    return LocalTimeUtil.create(endMinutesOfDay).asFormattedString()
}

internal fun TimeSlotItemUiState.timeLog(): String {
    return """
        ${this.getStartTimeText()}(${this.startMinutesOfDay}) ~ ${this.getEndTimeText()}(${this.endMinutesOfDay})
    """.trimIndent()
}

internal fun TimeSlotItemUiState.midMinute() : Int {
    return startMinutesOfDay + (endMinutesOfDay - startMinutesOfDay)
}