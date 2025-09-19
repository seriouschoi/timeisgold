package software.seriouschoi.timeisgold.feature.timeroutine.page

internal sealed interface TimeRoutinePageUiIntent {
    data class ShowSlotEdit(val slotId: String, val routineId: String) : TimeRoutinePageUiIntent

    object CreateRoutine : TimeRoutinePageUiIntent
    object ModifyRoutine : TimeRoutinePageUiIntent

}