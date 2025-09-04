package software.seriouschoi.timeisgold.feature.timeroutine.edit

import java.time.DayOfWeek

internal sealed interface TimeRoutineEditUiIntent {
    data object Save : TimeRoutineEditUiIntent
    data object Cancel : TimeRoutineEditUiIntent
    data object Exit : TimeRoutineEditUiIntent
    data object SaveConfirm : TimeRoutineEditUiIntent
    data class UpdateRoutineTitle(
        val title: String
    ) : TimeRoutineEditUiIntent

    data class UpdateDayOfWeek(
        val dayOfWeek: DayOfWeek, val checked: Boolean
    ) : TimeRoutineEditUiIntent
}