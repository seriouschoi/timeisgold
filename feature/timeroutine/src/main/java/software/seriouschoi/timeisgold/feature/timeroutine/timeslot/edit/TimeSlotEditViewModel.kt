package software.seriouschoi.timeisgold.feature.timeroutine.timeslot.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import software.seriouschoi.timeisgold.core.common.util.Envelope
import javax.inject.Inject

@HiltViewModel
internal class TimeSlotEditViewModel @Inject constructor(

) : ViewModel() {
    private val intentFlow = MutableSharedFlow<Envelope<TimeSlotEditIntent>>()

    // TODO: 파이프 라인 정의 하기.
    val uiState: StateFlow<TimeSlotEditUiState> = MutableStateFlow(TimeSlotEditUiState())

    fun sendIntent(intent: TimeSlotEditIntent) {
        viewModelScope.launch {
            intentFlow.emit(Envelope(intent))
        }
    }
}