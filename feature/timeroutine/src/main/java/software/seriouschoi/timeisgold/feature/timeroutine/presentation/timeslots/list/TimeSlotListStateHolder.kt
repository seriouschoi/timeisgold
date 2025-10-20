package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

internal class TimeSlotListStateHolder @Inject constructor(){
    private val _state = MutableStateFlow(TimeSlotListState())
    val state: StateFlow<TimeSlotListState> = _state




}