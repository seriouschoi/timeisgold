package software.seriouschoi.timeisgold.feature.timeroutine.presentation.edit

import java.time.DayOfWeek

internal sealed class TimeRoutineEditUiIntent(
) {
    data object Save : TimeRoutineEditUiIntent()
    data object Delete : TimeRoutineEditUiIntent()
    data object Cancel : TimeRoutineEditUiIntent()
    data object Exit : TimeRoutineEditUiIntent()
    data object SaveConfirm : TimeRoutineEditUiIntent()
    data object DeleteConfirm : TimeRoutineEditUiIntent()

    data class UpdateRoutineTitle(
        val title: String,
    ) : TimeRoutineEditUiIntent()

    data class UpdateDayOfWeek(
        val dayOfWeek: DayOfWeek, val checked: Boolean,
    ) : TimeRoutineEditUiIntent()
}