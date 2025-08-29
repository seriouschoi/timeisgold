package software.seriouschoi.timeisgold.feature.timeroutine.bar.timeroutine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.GetTimeRoutineUseCase
import java.time.DayOfWeek
import java.time.LocalTime
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 8. 26.
 * jhchoi
 */
@HiltViewModel
internal class TimeRoutineViewModel @Inject constructor(
    val getTimeRoutineUseChar: GetTimeRoutineUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<TimeRoutineUiState>(TimeRoutineUiState.Empty)
    val uiState = _uiState.asStateFlow()


    fun readTimeRoutine(dayOfWeek: DayOfWeek) {
        // TODO: jhchoi 2025. 8. 26. 이렇게 가져오지 말고 flow로 바로 가져올 방법을 찾아봐야..
        viewModelScope.launch {
            val timeRoutineDetailData = getTimeRoutineUseChar(dayOfWeek)
//            val timeRoutineData = timeRoutineDetailData?.timeRoutineData
//
//            timeRoutineDetailData?.timeSlotList?.map {
//                TimeSlotItemUiState(
//                    title = it.title,
//                    startTime = it.startTime,
//                    endTime = it.endTime
//                )
//            }
//
//            _uiState.value = TimeRoutineUiState.Routine(
//                title = timeRoutineData?.title ?: ""
//            )
        }
    }
}

internal sealed class TimeRoutineUiState {
    data class Routine(
        val title: String
    ) : TimeRoutineUiState()

    object Empty : TimeRoutineUiState()
}

internal data class TimeSlotItemUiState(
    val title: String,
    val startTime: LocalTime,
    val endTime: LocalTime
)
