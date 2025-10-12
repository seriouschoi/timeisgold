package software.seriouschoi.timeisgold.feature.timeroutine.presentation.edit.slot

import java.time.LocalTime

sealed interface TimeSlotEditIntent {
    data class UpdateSlotName(val newName: String) : TimeSlotEditIntent
    data class SelectTime(val time: LocalTime, val isStartTime: Boolean) : TimeSlotEditIntent
    data class SelectedTime(val selectedTime: LocalTime, val isStartTime: Boolean) :
        TimeSlotEditIntent

    data object Back : TimeSlotEditIntent
    data object Save : TimeSlotEditIntent
    data object Delete : TimeSlotEditIntent
    data object DeleteConfirm : TimeSlotEditIntent
    data object SaveConfirm : TimeSlotEditIntent

}
