package software.seriouschoi.timeisgold.feature.timeroutine.page

import java.time.DayOfWeek

internal sealed interface TimeRoutinePageUiIntent {
    data class CreateRoutine(val dayOfWeek: DayOfWeek) : TimeRoutinePageUiIntent

}