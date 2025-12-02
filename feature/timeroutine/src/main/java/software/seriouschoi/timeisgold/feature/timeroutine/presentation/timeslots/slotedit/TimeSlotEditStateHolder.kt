package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.slotedit

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import software.seriouschoi.timeisgold.domain.data.vo.TimeSlotVO
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

internal data class TimeSlotEditState(
    val slotUuid: String? = null,
    val title: String,

    val startTime: LocalTime,
    val endTime: LocalTime,

    val selectableStartTimeRange: Pair<LocalTime, LocalTime> = LocalTime.MIN to LocalTime.MAX,
    val selectableEndTimeRange: Pair<LocalTime, LocalTime> = LocalTime.MIN to LocalTime.MAX,
)

internal fun TimeSlotEditState.toVo(): TimeSlotVO {
    return TimeSlotVO(
        title = title,
        startTime = startTime,
        endTime = endTime
    )
}
