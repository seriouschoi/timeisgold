package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.slotedit

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalTime
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 10. 21.
 * jhchoi
 */
internal class TimeSlotEditStateHolder @Inject constructor() {
    private val _state = MutableStateFlow<TimeSlotEditState?>(
        null
    )

    val state: StateFlow<TimeSlotEditState?> = _state

    fun show(state: TimeSlotEditState) {
        _state.update {
            state
        }
    }

    fun changeSlotId(slotId: String) {
        _state.update {
            it?.copy(
                slotUuid = slotId
            )
        }
    }

    fun changeTitle(title: String) {
        _state.update {
            it?.copy(
                title = title
            )
        }
    }

    fun changeStartTime(startTime: LocalTime) {
        _state.update {
            it?.copy(
                startTime = startTime
            )
        }
    }

    fun changeEndTime(endTime: LocalTime) {
        _state.update {
            it?.copy(
                endTime = endTime
            )
        }
    }

    fun clear() {
        _state.update {
            null
        }
    }
}

@Deprecated("Deprecated use simple method")
internal sealed interface TimeSlotEditStateIntent {
    data class Init(
        val state: TimeSlotEditState
    ) : TimeSlotEditStateIntent

    data class Update(
        val slotId: String? = null,
        val slotTitle: String? = null,
        val startTime: LocalTime? = null,
        val endTime: LocalTime? = null
    ) : TimeSlotEditStateIntent

    data object Clear : TimeSlotEditStateIntent
}

internal data class TimeSlotEditState(
    val slotUuid: String? = null,
    val title: String,

    val startTime: LocalTime,
    val endTime: LocalTime,

    val startTimeRange: Pair<LocalTime, LocalTime> = LocalTime.MIN to LocalTime.MAX,
    val endTimeRange: Pair<LocalTime, LocalTime> = LocalTime.MIN to LocalTime.MAX,
)
