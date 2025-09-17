package software.seriouschoi.timeisgold.feature.timeroutine.timeslot.edit

sealed interface TimeSlotEditIntent {
    data class UpdateSlotName(val newName: String) : TimeSlotEditIntent

    data object Back : TimeSlotEditIntent
    data object Save : TimeSlotEditIntent
    data object Delete : TimeSlotEditIntent

}
