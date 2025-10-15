package software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager.stateholder

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.core.common.util.asShortText
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.dayofweeks.DayOfWeekItemUiState
import java.time.DayOfWeek
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 10. 14.
 * jhchoi
 */
internal class RoutineDayOfWeeksStateHolder @Inject constructor() {
    private val _state = MutableStateFlow(RoutineDayOfWeeksState())
    val state: StateFlow<RoutineDayOfWeeksState> = _state

    val checkedDayOfWeeks = state.map {
        it.dayOfWeeksList.filter {
            it.checked && it.enabled
        }.map { it.dayOfWeek }
    }

    init {
        val dayOfWeekStateList = DAY_OF_WEEKS.map {
            val dayOfWeekName = it.asShortText()
            DayOfWeekItemUiState(
                displayName = UiText.Raw(dayOfWeekName),
                enabled = true,
                checked = false,
                dayOfWeek = it
            )
        }
        _state.update {
            it.copy(dayOfWeeksList = dayOfWeekStateList)
        }
    }

    fun reduce(intent: RoutineDayOfWeeksIntent) {
        when (intent) {
            is RoutineDayOfWeeksIntent.Update -> {
                _state.update { state: RoutineDayOfWeeksState ->
                    val newList = state.dayOfWeeksList.map {
                        val checked = intent.checked.contains(it.dayOfWeek)
                        val enabled = intent.enabled.contains(it.dayOfWeek)
                        it.copy(checked = checked || !enabled, enabled = enabled)
                    }
                    state.copy(dayOfWeeksList = newList)
                }
            }

            is RoutineDayOfWeeksIntent.Check -> {
                _state.update { state: RoutineDayOfWeeksState ->
                    val newList = state.dayOfWeeksList.map {
                        if (it.dayOfWeek == intent.dayOfWeek) {
                            it.copy(checked = intent.checked)
                        } else {
                            it
                        }
                    }
                    state.copy(dayOfWeeksList = newList)
                }
            }
        }
    }

    companion object {
        private val DAY_OF_WEEKS = listOf(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY,
            DayOfWeek.SUNDAY
        )
    }
}

internal data class RoutineDayOfWeeksState(
    val dayOfWeeksList: List<DayOfWeekItemUiState> = emptyList()
)

internal sealed interface RoutineDayOfWeeksIntent {
    data class Update(
        val checked: List<DayOfWeek>, val enabled: List<DayOfWeek>
    ) : RoutineDayOfWeeksIntent

    data class Check(
        val dayOfWeek: DayOfWeek,
        val checked: Boolean
    ) : RoutineDayOfWeeksIntent
}
