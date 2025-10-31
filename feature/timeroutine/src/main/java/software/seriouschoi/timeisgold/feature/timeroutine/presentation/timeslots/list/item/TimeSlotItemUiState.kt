package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.item

import software.seriouschoi.timeisgold.core.common.util.LocalTimeUtil
import software.seriouschoi.timeisgold.core.common.util.asFormattedString

internal data class TimeSlotItemUiState(
    val slotUuid: String,
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

internal fun TimeSlotItemUiState.midMinute() : Float {
    return startMinutesOfDay + ((endMinutesOfDay - startMinutesOfDay) / 2f)
}

internal fun TimeSlotItemUiState.splitOverMidnight(): List<TimeSlotItemUiState> {
    return if (this.startMinutesOfDay > this.endMinutesOfDay) {
        //ex: 22 ~ 6

        //24 + 6(endTime)
        val overEndMinutes = LocalTimeUtil.DAY_MINUTES + (this.endMinutesOfDay)
        //00 - (24 - 22(startTime))
        val negativeStartMinutes =
            0 - (LocalTimeUtil.DAY_MINUTES - this.startMinutesOfDay)
        listOf(
            this.copy(
                startMinutesOfDay = this.startMinutesOfDay,
                endMinutesOfDay = overEndMinutes,
            ),
            this.copy(
                startMinutesOfDay = negativeStartMinutes,
                endMinutesOfDay = this.endMinutesOfDay,
            )
        )
    } else {
        listOf(
            this
        )
    }
}