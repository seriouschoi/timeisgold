package software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager.routine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import software.seriouschoi.timeisgold.core.common.ui.ResultState
import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.core.common.ui.asResultState
import software.seriouschoi.timeisgold.core.domain.mapper.onlyDomainSuccess
import software.seriouschoi.timeisgold.core.domain.mapper.onlyResultSuccess
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineDefinition
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.WatchAllRoutineDayOfWeeksUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.WatchTimeRoutineDefinitionUseCase
import software.seriouschoi.timeisgold.feature.timeroutine.data.TimeRoutineFeatureState
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 10. 9.
 * jhchoi
 */
@HiltViewModel
internal class TimeRoutineDefinitionViewModel @Inject constructor(
    private val state: TimeRoutineFeatureState,
    private val watchTimeRoutineUseCase: WatchTimeRoutineDefinitionUseCase
) : ViewModel() {

    private val currentDayOfWeek = state.data.map {
        it.dayOfWeek
    }.asResultState().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        ResultState.Loading
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val routineDefinition = currentDayOfWeek.onlyResultSuccess()
        .mapNotNull { it }.flatMapLatest {
            watchTimeRoutineUseCase.invoke(it)
        }.asResultState().stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            ResultState.Loading
        )



    val uiState = merge(
        routineDefinition.map {
            UiPreState.RoutineDefinition(it)
        },
    ).scan(
        TimeRoutineDefinitionUiState()
    ) { acc, value: UiPreState ->
        acc.reduceFrom(value)
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        TimeRoutineDefinitionUiState()
    )

}

private fun TimeRoutineDefinitionUiState.reduceFrom(value: UiPreState): TimeRoutineDefinitionUiState =
    when (value) {
        is UiPreState.RoutineDefinition -> {
            this.reduceFromDefinition(value)
        }
    }


private fun TimeRoutineDefinitionUiState.reduceFromDefinition(value: UiPreState.RoutineDefinition): TimeRoutineDefinitionUiState =
    when (value.state) {
        is ResultState.Loading -> {
            this.copy(
                loading = true
            )
        }

        else -> {
            val routine = value.state.onlyDomainSuccess()
            val title = routine?.timeRoutine?.title?.let {
                UiText.Raw(it)
            } ?: UiText.Raw("")

            this.copy(
                loading = false,
                title = title,
            )
        }
    }


private sealed interface UiPreState {
    data class RoutineDefinition(
        val state: ResultState<DomainResult<TimeRoutineDefinition>>
    ) : UiPreState
}