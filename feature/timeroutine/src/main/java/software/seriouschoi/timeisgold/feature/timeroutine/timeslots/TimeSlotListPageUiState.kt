package software.seriouschoi.timeisgold.feature.timeroutine.timeslots

import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.feature.timeroutine.timeslots.list.TimeSlotItemUiState
import java.time.DayOfWeek
import software.seriouschoi.timeisgold.core.common.ui.R as CommonR

internal sealed interface TimeRoutinePageUiState {
    data class Routine(
        val title: String = "",
        val dayOfWeekName: String,
        val dayOfWeeks: List<DayOfWeek> = listOf(),
        val slotItemList: List<TimeSlotItemUiState> = emptyList(),
    ) : TimeRoutinePageUiState {
        companion object {
            fun default(): Routine {
                return Routine(
                    title = "",
                    dayOfWeekName = "",
                    dayOfWeeks = emptyList()
                )
            }
        }
    }

    data class Loading(
        val loadingMessage: UiText,
    ) : TimeRoutinePageUiState {
        companion object {
            fun default(): Loading {
                return Loading(
                    UiText.MultipleResArgs.create(
                        CommonR.string.message_format_loading,
                        CommonR.string.text_routine
                    )
                )
            }
        }
    }

    data class Error(
        val errorMessage: UiText,
        val confirmButton: TimeRoutinePageButtonState? = null
    ) : TimeRoutinePageUiState
}

internal data class TimeRoutinePageButtonState(
    val buttonLabel: UiText,
    val intent: TimeRoutinePageUiIntent,
)