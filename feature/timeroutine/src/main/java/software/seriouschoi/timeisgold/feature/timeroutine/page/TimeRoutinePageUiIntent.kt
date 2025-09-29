package software.seriouschoi.timeisgold.feature.timeroutine.page

import java.time.LocalTime

internal sealed interface TimeRoutinePageUiIntent {
    data class ShowSlotEdit(val slotId: String, val routineId: String) : TimeRoutinePageUiIntent
    data class UpdateSlot(
        val uuid: String, val newStart: LocalTime, val newEnd: LocalTime, val onlyUi: Boolean
    ) : TimeRoutinePageUiIntent

    object CreateRoutine : TimeRoutinePageUiIntent
    object ModifyRoutine : TimeRoutinePageUiIntent

}