package software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import software.seriouschoi.timeisgold.core.common.ui.ResultState
import software.seriouschoi.timeisgold.core.common.ui.withResultStateLifecycle
import software.seriouschoi.timeisgold.core.common.util.MetaEnvelope
import software.seriouschoi.timeisgold.core.domain.mapper.asResultState
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.vo.TimeRoutineVO
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.SetRoutineUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.WatchRoutineUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.WatchSelectableDayOfWeeksUseCase
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.components.dayofweeks.check.DayOfWeeksCheckStateHolder
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.components.dayofweeks.pager.DayOfWeeksPagerStateHolder
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager.stateholder.RoutineTitleStateHolder
import timber.log.Timber
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 8. 26.
 * jhchoi
 */
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
internal class TimeRoutinePagerViewModel @Inject constructor(
    private val dayOfWeeksPagerStateHolder: DayOfWeeksPagerStateHolder,
    private val routineTitleStateHolder: RoutineTitleStateHolder,
    private val routineDayOfWeeksStateHolder: DayOfWeeksCheckStateHolder,

    private val watchRoutineUseCase: WatchRoutineUseCase,
    private val watchSelectableDayOfWeeksUseCase: WatchSelectableDayOfWeeksUseCase,
    private val setRoutineUseCase: SetRoutineUseCase
) : ViewModel() {

    private val _intent = MutableSharedFlow<MetaEnvelope<TimeRoutinePagerUiIntent>>()

    private val currentRoutine = dayOfWeeksPagerStateHolder.state.map {
        it.currentDayOfWeek
    }.flatMapLatest {
        watchRoutineUseCase.invoke(it)
    }.map {
        it.asResultState()
    }.withResultStateLifecycle().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = ResultState.Loading
    )

    val uiState = combine(
        routineTitleStateHolder.state,
        routineDayOfWeeksStateHolder.state,
        dayOfWeeksPagerStateHolder.state
    ) { title, routineDayOfWeeks, dayOfWeeks ->
        TimeRoutinePagerUiState(
            dayOfWeekState = dayOfWeeks,
            titleState = title,
            routineDayOfWeeks = routineDayOfWeeks
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = TimeRoutinePagerUiState()
    )

    init {
        dayOfWeeksPagerStateHolder.select(DayOfWeek.from(LocalDate.now()))

        watchIntent()

        watchCurrentRoutine()
        watchRoutineDayOfWeeks()
    }

    fun sendIntent(intent: TimeRoutinePagerUiIntent) {
        viewModelScope.launch {
            this@TimeRoutinePagerViewModel._intent.emit(MetaEnvelope(intent))
        }
    }

    private fun watchRoutineDayOfWeeks() {
        val currentRoutine = currentRoutine.mapNotNull { (it as? ResultState.Success)?.data }
        val selectableDayOfWeeks = dayOfWeeksPagerStateHolder.state.map {
            it.currentDayOfWeek
        }.flatMapLatest {
            watchSelectableDayOfWeeksUseCase.invoke(it)
        }

        combine(
            selectableDayOfWeeks.mapNotNull {
                (it as? DomainResult.Success)?.value
            }.distinctUntilChanged(),
            currentRoutine.map {
                it.payload.dayOfWeeks
            }.distinctUntilChanged()
        ) { enableDayOfWeeks, routineDayOfWeeks ->
            Pair(enableDayOfWeeks, routineDayOfWeeks)
        }.onEach {
            routineDayOfWeeksStateHolder.update(
                enabled = it.first,
                checked = it.second
            )
        }.launchIn(viewModelScope)
    }

    private fun watchCurrentRoutine() {
        currentRoutine.mapNotNull {
            when(it) {
                is ResultState.Loading,
                is ResultState.Error -> {
                    null
                }
                is ResultState.Success -> {
                    it.data?.payload?.title ?: ""
                }
            }
        }.onEach {
            routineTitleStateHolder.updateTitle(it)
        }.launchIn(viewModelScope)
    }

    private fun watchIntent() = viewModelScope.launch {
        _intent.onEach {
            handleIntentSideEffect(it.payload)
        }.onEach {
            when (it.payload) {
                is TimeRoutinePagerUiIntent.CheckDayOfWeek,
                is TimeRoutinePagerUiIntent.UpdateRoutineTitle -> {
                    saveRoutine()
                }

                else -> {
                    //no work.
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun handleIntentSideEffect(intent: TimeRoutinePagerUiIntent) {
        when (intent) {
            is TimeRoutinePagerUiIntent.SelectCurrentDayOfWeek -> {
                dayOfWeeksPagerStateHolder.select(intent.currentDayOfWeek)
            }

            is TimeRoutinePagerUiIntent.CheckDayOfWeek -> {
                routineDayOfWeeksStateHolder.check(
                    dayOfWeeks = intent.dayOfWeek,
                    checked = intent.isCheck
                )
            }

            is TimeRoutinePagerUiIntent.UpdateRoutineTitle -> {
                Timber.d("handleIntentSideEffect - UpdateRoutineTitle=$intent")
                routineTitleStateHolder.updateTitle(intent.title)
            }
        }
    }

    private fun saveRoutine() {
        flow {
            //레이스 컨디션에 의해 잘못된 값을 저장하는 것을 방지를 위해, 저장할 값들을 직접 읽어온다.
            val currentDayOfWeek = dayOfWeeksPagerStateHolder.state.first().currentDayOfWeek
            val routineTitle = routineTitleStateHolder.state.first().title
            val routineDayOfWeeks =
                routineDayOfWeeksStateHolder.state.first().dayOfWeeksList.filter {
                    it.checked && it.enabled
                }.map { it.dayOfWeek }.toSet()

            val result = setRoutineUseCase.invoke(
                routineVO = TimeRoutineVO(
                    title = routineTitle,
                    dayOfWeeks = routineDayOfWeeks
                ),
                dayOfWeek = currentDayOfWeek
            )
            emit(result)
        }.map {
            it.asResultState()
        }.withResultStateLifecycle().onEach {
            /* 저장 상태 처리. */
        }.launchIn(viewModelScope)
    }
}