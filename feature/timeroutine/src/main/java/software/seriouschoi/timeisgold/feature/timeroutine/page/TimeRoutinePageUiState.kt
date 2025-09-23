package software.seriouschoi.timeisgold.feature.timeroutine.page

import software.seriouschoi.timeisgold.core.common.ui.UiText
import java.time.DayOfWeek
import software.seriouschoi.timeisgold.core.common.ui.R as CommonR

internal sealed interface TimeRoutinePageUiState {
    data class Routine(
        val title: String = "",
        val dayOfWeekName: String,
        val slotItemList: List<TimeSlotCardUiState> = emptyList(),
        val dayOfWeeks: List<DayOfWeek> = listOf(),
    ) : TimeRoutinePageUiState {
        companion object {
            fun default(): Routine {
                return Routine(
                    title = "",
                    dayOfWeekName = "",
                    slotItemList = emptyList(),
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