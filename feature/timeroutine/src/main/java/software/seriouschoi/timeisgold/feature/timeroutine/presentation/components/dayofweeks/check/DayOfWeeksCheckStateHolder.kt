package software.seriouschoi.timeisgold.feature.timeroutine.presentation.components.dayofweeks.check

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.core.common.util.asShortText
import java.time.DayOfWeek
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 10. 14.
 * jhchoi
 */
internal class DayOfWeeksCheckStateHolder @Inject constructor() {
    private val _state = MutableStateFlow(DayOfWeeksCheckState())
    val state: StateFlow<DayOfWeeksCheckState> = _state

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

    fun reduce(intent: DayOfWeeksCheckIntent) {
        when (intent) {
            is DayOfWeeksCheckIntent.Update -> {
                _state.update { state: DayOfWeeksCheckState ->
                    val newList = state.dayOfWeeksList.map {
                        val checked = intent.checked.contains(it.dayOfWeek)
                        val enabled = intent.enabled.contains(it.dayOfWeek)
                        it.copy(checked = checked || !enabled, enabled = enabled)
                    }
                    state.copy(dayOfWeeksList = newList)
                }
            }

            is DayOfWeeksCheckIntent.Check -> {
                _state.update { state: DayOfWeeksCheckState ->
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

internal data class DayOfWeeksCheckState(
    val dayOfWeeksList: List<DayOfWeekItemUiState> = emptyList()
)

internal data class DayOfWeekItemUiState(
    val displayName: UiText,
    val enabled: Boolean,
    val checked: Boolean,
    val dayOfWeek: DayOfWeek
)

internal sealed interface DayOfWeeksCheckIntent {
    data class Update(
        val checked: Collection<DayOfWeek>, val enabled: Collection<DayOfWeek>
    ) : DayOfWeeksCheckIntent

    data class Check(
        val dayOfWeek: DayOfWeek,
        val checked: Boolean
    ) : DayOfWeeksCheckIntent
}
