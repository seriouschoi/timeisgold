package software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager.routine

import java.time.DayOfWeek

/**
 * Created by jhchoi on 2025. 10. 10.
 * jhchoi
 */
internal sealed interface TimeRoutineDefinitionIntent {
    data class EditTitle(val title: String) : TimeRoutineDefinitionIntent
    data class EditDayOfWeek(
        val dayOfWeek: DayOfWeek,
        val checked: Boolean
    ) : TimeRoutineDefinitionIntent
}
