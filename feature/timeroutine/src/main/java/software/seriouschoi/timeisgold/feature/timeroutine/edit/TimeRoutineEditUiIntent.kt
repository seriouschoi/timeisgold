package software.seriouschoi.timeisgold.feature.timeroutine.edit

internal sealed interface TimeRoutineEditUiIntent {
    data object Save : TimeRoutineEditUiIntent
    data object Cancel : TimeRoutineEditUiIntent
    data object Exit : TimeRoutineEditUiIntent
    data object SaveConfirm : TimeRoutineEditUiIntent
}