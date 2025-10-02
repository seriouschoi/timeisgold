package software.seriouschoi.timeisgold.feature.timeroutine.page

internal sealed interface TimeRoutinePageUiIntent {
    data class ShowSlotEdit(val slotId: String, val routineId: String) : TimeRoutinePageUiIntent
    data class UpdateTimeSlotUi(
        val uuid: String,
        val newStart: Int,
        val newEnd: Int,
        val orderChange: Boolean
    ) : TimeRoutinePageUiIntent

    data object UpdateTimeSlotList : TimeRoutinePageUiIntent

    object CreateRoutine : TimeRoutinePageUiIntent
    object ModifyRoutine : TimeRoutinePageUiIntent
}


