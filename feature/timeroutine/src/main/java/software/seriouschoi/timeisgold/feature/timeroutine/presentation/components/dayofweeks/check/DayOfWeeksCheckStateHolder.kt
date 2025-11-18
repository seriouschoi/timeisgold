package software.seriouschoi.timeisgold.feature.timeroutine.presentation.components.dayofweeks.check

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.core.common.util.asShortText
import timber.log.Timber
import java.time.DayOfWeek
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 10. 14.
 * jhchoi
 */
internal class DayOfWeeksCheckStateHolder @Inject constructor() {
    private val _state = MutableStateFlow(DayOfWeeksCheckState())
    val state: StateFlow<DayOfWeeksCheckState> = _state

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

    fun check(dayOfWeeks: DayOfWeek, checked: Boolean) {
        Timber.d("check - dayOfWeeks=$dayOfWeeks")
        _state.update { state: DayOfWeeksCheckState ->
            val newList = state.dayOfWeeksList.map {
                if (it.dayOfWeek == dayOfWeeks) {
                    it.copy(checked = checked)
                } else {
                    it
                }
            }
            state.copy(dayOfWeeksList = newList)
        }
    }

    fun update(checked: Collection<DayOfWeek>, enabled: Collection<DayOfWeek>) {
        Timber.d("update - checked=$checked, enabled=$enabled")
        _state.update { state: DayOfWeeksCheckState ->
            val newList = state.dayOfWeeksList.map {
                val checked = checked.contains(it.dayOfWeek)
                val enabled = enabled.contains(it.dayOfWeek)
                it.copy(checked = checked || !enabled, enabled = enabled)
            }
            state.copy(dayOfWeeksList = newList)
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