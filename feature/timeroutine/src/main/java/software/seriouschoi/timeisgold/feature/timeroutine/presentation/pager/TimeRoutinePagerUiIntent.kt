package software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager

import java.time.DayOfWeek

sealed interface TimeRoutinePagerUiIntent {
    data class LoadRoutine(val dayOfWeek: DayOfWeek) : TimeRoutinePagerUiIntent

    @Deprecated("루틴 수정/추가는 루틴 뷰에서 처리.")
    object ModifyRoutine : TimeRoutinePagerUiIntent

    @Deprecated("루틴 수정/추가는 루틴 뷰에서 처리.")
    object AddRoutine : TimeRoutinePagerUiIntent

    data class UpdateRoutineTitle(val title: String) : TimeRoutinePagerUiIntent
    data class CheckDayOfWeek(
        val checked: Boolean, val dayOfWeek: DayOfWeek
    ) : TimeRoutinePagerUiIntent
}
