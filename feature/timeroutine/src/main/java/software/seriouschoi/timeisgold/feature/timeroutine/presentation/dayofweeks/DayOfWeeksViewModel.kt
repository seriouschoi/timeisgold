package software.seriouschoi.timeisgold.feature.timeroutine.presentation.dayofweeks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import software.seriouschoi.timeisgold.core.common.ui.ResultState
import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.core.common.ui.asResultState
import software.seriouschoi.timeisgold.core.domain.mapper.onlyDomainSuccess
import software.seriouschoi.timeisgold.core.domain.mapper.onlyResultSuccess
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.WatchAllRoutineDayOfWeeksUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.WatchTimeRoutineDefinitionUseCase
import software.seriouschoi.timeisgold.feature.timeroutine.data.TimeRoutineFeatureState
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 10. 12.
 * jhchoi
 */
@HiltViewModel
internal class DayOfWeeksViewModel @Inject constructor(
    private val state: TimeRoutineFeatureState,
    private val allDayOfWeeksUseCase: WatchAllRoutineDayOfWeeksUseCase,
    private val watchTimeRoutineUseCase: WatchTimeRoutineDefinitionUseCase
) : ViewModel() {

    private val intent = MutableSharedFlow<DayOfWeeksIntent>()

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

    private val allExistDays = allDayOfWeeksUseCase.invoke().asResultState().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        ResultState.Loading
    )

    private val preUiStateFromDomain = combine(
        routineDefinition, allExistDays
    ) { routine, days ->

        val routineDayOfWeeks = routine.onlyDomainSuccess()?.dayOfWeeks?.map {
            it.dayOfWeek
        } ?: emptyList()
        val allDayOfWeeks = days.onlyDomainSuccess() ?: return@combine null

        val dayOfWeeks = DayOfWeek.entries.map { dayOfWeek ->
            val dayOfWeekName = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            val checked = routineDayOfWeeks.contains(dayOfWeek)
            val enabled = !allDayOfWeeks.contains(dayOfWeek) || checked
            DayOfWeekItemUiState(
                displayName = UiText.Raw(dayOfWeekName),
                checked = checked || !enabled,
                enabled = enabled,
                id = dayOfWeek
            )
        }
        UiPreState.DayOfWeeks(dayOfWeeks = dayOfWeeks)
    }.mapNotNull { it }.asResultState().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        ResultState.Loading
    )

    val uiState = merge(
        preUiStateFromDomain,
        intent.mapNotNull {
            it as? DayOfWeeksIntent.EditDayOfWeek ?: return@mapNotNull null
        }.map { it: DayOfWeeksIntent.EditDayOfWeek ->
            UiPreState.SelectDayOfWeek(it.dayOfWeek, it.checked)
        }.asResultState()
    ).scan(DayOfWeeksUiState(isLoading = true)) { acc, value: ResultState<UiPreState> ->
        val result = value.onlyResultSuccess() ?: return@scan acc
        when (result) {
            is UiPreState.DayOfWeeks -> {
                acc.copy(
                    isLoading = false,
                    dayOfWeekList = result.dayOfWeeks
                )
            }

            is UiPreState.SelectDayOfWeek -> {
                val dayOfWeekList = acc.dayOfWeekList.map {
                    if (it.id == result.dayOfWeek) {
                        it.copy(checked = result.checked)
                    } else {
                        it
                    }
                }
                acc.copy(
                    dayOfWeekList = dayOfWeekList
                )
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        DayOfWeeksUiState(isLoading = true)
    )

    init {
        viewModelScope.launch {
            intent.collect {
                handleIntent(it)
            }
        }
    }

    private fun handleIntent(intent: DayOfWeeksIntent) {
        when (intent) {
            is DayOfWeeksIntent.EditDayOfWeek -> {
                // TODO: jhchoi 2025. 10. 12. update day of weeks.
            }
        }
    }

    fun sendIntent(intent: DayOfWeeksIntent) {
        viewModelScope.launch {
            this@DayOfWeeksViewModel.intent.emit(intent)
        }
    }
}

private sealed interface UiPreState {
    data class DayOfWeeks(
        val dayOfWeeks: List<DayOfWeekItemUiState>
    ) : UiPreState

    data class SelectDayOfWeek(val dayOfWeek: DayOfWeek, val checked: Boolean) : UiPreState
}