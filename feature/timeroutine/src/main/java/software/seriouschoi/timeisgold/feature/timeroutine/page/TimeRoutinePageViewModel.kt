package software.seriouschoi.timeisgold.feature.timeroutine.page

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import software.seriouschoi.navigator.DestNavigatorPort
import software.seriouschoi.timeisgold.core.common.ui.ResultState
import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.core.common.ui.asResultState
import software.seriouschoi.timeisgold.core.common.ui.flowResultState
import software.seriouschoi.timeisgold.core.common.util.Envelope
import software.seriouschoi.timeisgold.core.common.util.LocalTimeUtil
import software.seriouschoi.timeisgold.core.common.util.asMinutes
import software.seriouschoi.timeisgold.core.domain.mapper.onlyDomainResult
import software.seriouschoi.timeisgold.core.domain.mapper.onlySuccess
import software.seriouschoi.timeisgold.core.domain.mapper.toUiText
import software.seriouschoi.timeisgold.domain.data.DomainError
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.data.entities.TimeSlotEntity
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.WatchTimeRoutineCompositionUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.NormalizeForUiUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.SetTimeSlotListUseCase
import software.seriouschoi.timeisgold.feature.timeroutine.edit.TimeRoutineEditScreenRoute
import software.seriouschoi.timeisgold.feature.timeroutine.timeslot.edit.TimeSlotEditScreenRoute
import timber.log.Timber
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject
import software.seriouschoi.timeisgold.core.common.ui.R as CommonR

/**
 * Created by jhchoi on 2025. 8. 26.
 * jhchoi
 */
@HiltViewModel
internal class TimeRoutinePageViewModel @Inject constructor(
    private val navigator: DestNavigatorPort,
    private val watchTimeRoutineCompositionUseCase: WatchTimeRoutineCompositionUseCase,
    private val setTimeSlotsUseCase: SetTimeSlotListUseCase,
    private val normalizeForUiUseCase: NormalizeForUiUseCase,
) : ViewModel() {

    private val dayOfWeekFlow = MutableStateFlow<DayOfWeek?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val routineCompositionFlow: StateFlow<ResultState<DomainResult<TimeRoutineComposition>>> =
        dayOfWeekFlow.mapNotNull { it }.flatMapConcat {
            watchTimeRoutineCompositionUseCase.invoke(it)
        }.onEach {
            Timber.d("received routine composition.")
        }.asResultState().stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = ResultState.Loading
        )

    private val routinePreUiStateFlow = combine(
        dayOfWeekFlow.mapNotNull { it },
        routineCompositionFlow.onlyDomainResult().mapNotNull { it }
    ) { dayOfWeek, routineComposition ->
        UiPreState.Routine(
            currentDayOfWeek = dayOfWeek,
            routineDomainResult = routineComposition
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )


    private val _intent = MutableSharedFlow<Envelope<TimeRoutinePageUiIntent>>()

    private val _timeSlotUpdatePreUiStateFlow = MutableSharedFlow<UiPreState.UpdateSlotList>()

    val uiState: StateFlow<TimeRoutinePageUiState> = merge(
        routinePreUiStateFlow.mapNotNull { it },
        _intent.mapNotNull {
            UiPreState.Intent(it.payload)
        },
        _timeSlotUpdatePreUiStateFlow
    ).scan(
        TimeRoutinePageUiState.Loading.default()
    ) { acc: TimeRoutinePageUiState, value: UiPreState ->
        acc.reduce(value)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = TimeRoutinePageUiState.Loading.default()
    )
    private val _uiEvent: MutableSharedFlow<Envelope<TimeRoutinePageUiEvent>> = MutableSharedFlow()
    val uiEvent: SharedFlow<Envelope<TimeRoutinePageUiEvent>> = _uiEvent

    init {
        viewModelScope.launch {
            _intent.collect {
                handleIntentSideEffect(it.payload)
            }
        }
    }

    fun load(dayOfWeek: DayOfWeek) {
        viewModelScope.launch {
            dayOfWeekFlow.emit(dayOfWeek)
        }
    }


    private suspend fun handleIntentSideEffect(intent: TimeRoutinePageUiIntent) {
        when (intent) {
            is TimeRoutinePageUiIntent.ModifyRoutine,
            is TimeRoutinePageUiIntent.CreateRoutine,
                -> {
                val dayOfWeekOrdinal = dayOfWeekFlow.value?.ordinal
                if (dayOfWeekOrdinal != null)
                    navigator.navigate(TimeRoutineEditScreenRoute(dayOfWeekOrdinal))
            }

            is TimeRoutinePageUiIntent.ShowSlotEdit -> {
                val route = TimeSlotEditScreenRoute(
                    timeSlotUuid = intent.slotId,
                    timeRoutineUuid = intent.routineId
                )
                navigator.navigate(route)
            }

            is TimeRoutinePageUiIntent.UpdateTimeSlotList -> {
                updateTimeSlotList()
            }

            is TimeRoutinePageUiIntent.UpdateTimeSlotUi -> {
                handleUpdateTimeSlot(intent)
            }
        }
    }

    private suspend fun handleUpdateTimeSlot(
        intent: TimeRoutinePageUiIntent.UpdateTimeSlotUi,
    ) {
        val routineState = uiState.value as? TimeRoutinePageUiState.Routine ?: return

        val currentSlotItemList = routineState.slotItemList
        val intentItem = currentSlotItemList.find { it.slotUuid == intent.uuid }?.copy(
            isSelected = true
        )?.copy(
            startMinutesOfDay = normalizeForUiUseCase.invoke(intent.newStart),
            endMinutesOfDay = normalizeForUiUseCase.invoke(intent.newEnd),
        ) ?: return

        val updateResult = updateSlotListState(
            intentItem, currentSlotItemList, intent.orderChange
        )
        val newUpdateList = updateResult.first.map {
            it.splitOverMidnight()
        }.flatten().distinct()

        _timeSlotUpdatePreUiStateFlow.emit(
            UiPreState.UpdateSlotList(newUpdateList)
        )
        updateResult.second?.let {
            _uiEvent.emit(Envelope(it))
        }
    }

    private fun updateSlotListState(
        updateItem: TimeSlotItemUiState,
        slotItemList: List<TimeSlotItemUiState>,
        orderChange: Boolean
    ): Pair<List<TimeSlotItemUiState>, TimeRoutinePageUiEvent?> {
        //오버랩 있음.
        Timber.d("updateSlotListState updateItem=${updateItem.timeLog()}")
        val updateSourceTime = slotItemList.find {
            updateItem.slotUuid == it.slotUuid
        } ?: return Pair(slotItemList, null)

        val overlapItem = slotItemList.find {
            if (updateItem.slotUuid == it.slotUuid) return@find false
            else
                LocalTimeUtil.overlab(
                    updateItem.startMinutesOfDay until updateItem.endMinutesOfDay,
                    it.startMinutesOfDay until it.endMinutesOfDay
                )
        }

        if (overlapItem == null) {
            //오버랩 없음.
            val list = slotItemList.map {
                if (it.slotUuid == updateItem.slotUuid) updateItem
                else it
            }
            return Pair(list, null)
        }

        //오버랩 있음.
        Timber.d(
            """
                updateSlotListState
                overlapItem=${overlapItem.timeLog()}
            """.trimIndent()
        )
        val intentItemMinutes = updateItem.run { this.endMinutesOfDay - this.startMinutesOfDay }
        if (orderChange) {
            //오버랩 아이템 순번 전환.

            val overlapItemMinutes =
                overlapItem.run { this.endMinutesOfDay - this.startMinutesOfDay }

            val newUpdateItem: TimeSlotItemUiState
            val newOverlapItem: TimeSlotItemUiState
            when {
                updateSourceTime.midMinute() < updateItem.midMinute() -> {
                    //down to up.
                    newUpdateItem = updateSourceTime.copy(
                        startMinutesOfDay = overlapItem.startMinutesOfDay,
                        endMinutesOfDay = overlapItem.startMinutesOfDay + intentItemMinutes
                    )
                    newOverlapItem = overlapItem.copy(
                        startMinutesOfDay = updateSourceTime.endMinutesOfDay - overlapItemMinutes,
                        endMinutesOfDay = updateSourceTime.endMinutesOfDay,
                    )
                }
                updateSourceTime.midMinute() > updateItem.midMinute() -> {
                    //up to down
                    newUpdateItem = updateSourceTime.copy(
                        startMinutesOfDay = overlapItem.endMinutesOfDay - intentItemMinutes,
                        endMinutesOfDay = overlapItem.endMinutesOfDay
                    )
                    newOverlapItem = overlapItem.copy(
                        startMinutesOfDay = updateSourceTime.startMinutesOfDay,
                        endMinutesOfDay = updateSourceTime.startMinutesOfDay + overlapItemMinutes,
                    )
                }
                else -> {
                    newUpdateItem = updateSourceTime
                    newOverlapItem = overlapItem
                }
            }

            Timber.d("timeslot order changed. newUpdateItem=${newUpdateItem.timeLog()}, newOverlapItem=${newOverlapItem.timeLog()}")

            val list = slotItemList.map {
                when (it.slotUuid) {
                    newOverlapItem.slotUuid -> newOverlapItem
                    newUpdateItem.slotUuid -> newUpdateItem
                    else -> it
                }
            }
            return Pair(list, TimeRoutinePageUiEvent.TimeSlotDragCursorRefresh(newUpdateItem))
        } else {
            //오버랩. 확장 제한.
            val newUpdateItem = if (updateItem.startMinutesOfDay > overlapItem.startMinutesOfDay) {
                // 아래에서 위로 드래그.
                updateItem.copy(
                    startMinutesOfDay = overlapItem.endMinutesOfDay
                )
            } else {
                // 위에서 아래로 드래그.
                updateItem.copy(
                    endMinutesOfDay = overlapItem.startMinutesOfDay,
                )
            }
            Timber.d("timeslot overlap changed. newUpdateItem=${newUpdateItem.startMinutesOfDay}~${newUpdateItem.endMinutesOfDay}")
            val list = slotItemList.map {
                when (it.slotUuid) {
                    newUpdateItem.slotUuid -> newUpdateItem
                    else -> it
                }
            }
            return Pair(list, null)
        }
    }

    private fun updateTimeSlotList() {
        flowResultState {
            val currentRoutine =
                routineCompositionFlow.onlySuccess().first()
                    ?: return@flowResultState DomainResult.Failure(DomainError.NotFound.TimeRoutine)

            val routineState = uiState.first() as? TimeRoutinePageUiState.Routine
                ?: return@flowResultState DomainResult.Failure(
                    DomainError.NotFound.TimeSlot
                )

            val updateSlots = routineState.slotItemList.map {
                TimeSlotEntity(
                    uuid = it.slotUuid,
                    title = it.title,
                    startTime = LocalTimeUtil.create(it.startMinutesOfDay),
                    endTime = LocalTimeUtil.create(it.endMinutesOfDay),
                    createTime = System.currentTimeMillis()
                )
            }

            setTimeSlotsUseCase.invoke(
                timeRoutineUuid = currentRoutine.timeRoutine.uuid,
                timeSlotList = updateSlots
            )
        }.onlyDomainResult().onEach { domainResult ->
            when (domainResult) {
                is DomainResult.Failure -> {
                    TimeRoutinePageUiEvent.ShowToast(
                        domainResult.error.toUiText(),
                        Toast.LENGTH_SHORT
                    )
                }

                is DomainResult.Success -> {
                    TimeRoutinePageUiEvent.ShowToast(
                        UiText.MultipleResArgs.create(
                            CommonR.string.message_format_complete,
                            CommonR.string.text_save,
                        ),
                        Toast.LENGTH_SHORT
                    )
                }

                null -> {
                    //no work
                    null
                }
            }?.let {
                _uiEvent.emit(Envelope(it))
            }
        }.launchIn(viewModelScope)
    }

    fun sendIntent(createRoutine: TimeRoutinePageUiIntent) {
        viewModelScope.launch {
            _intent.emit(Envelope(createRoutine))
        }
    }
}

private fun TimeRoutinePageUiState.reduce(value: UiPreState): TimeRoutinePageUiState {
    return when (value) {
        is UiPreState.Intent -> {
            this.reduce(value)
        }

        is UiPreState.Routine -> {
            this.reduce(value)
        }

        is UiPreState.UpdateSlotList -> {
            if (this is TimeRoutinePageUiState.Routine)
                this.copy(slotItemList = value.timeSlotList)
            else
                this
        }
    }
}

private fun TimeRoutinePageUiState.reduce(
    value: UiPreState.Intent,
): TimeRoutinePageUiState = when (value.intent) {
    else -> this
}

private fun TimeRoutinePageUiState.reduce(value: UiPreState.Routine): TimeRoutinePageUiState {
    val domainResult = value.routineDomainResult
    return when (domainResult) {
        is DomainResult.Failure -> {
            TimeRoutinePageUiState.Error(
                errorMessage = UiText.Res.create(
                    CommonR.string.message_format_routine_create_confirm,
                    value.currentDayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
                ),
                confirmButton = TimeRoutinePageButtonState(
                    buttonLabel = UiText.Res.create(CommonR.string.text_confirm),
                    intent = TimeRoutinePageUiIntent.ModifyRoutine
                )
            )
        }

        is DomainResult.Success -> {
            val routineState =
                this as? TimeRoutinePageUiState.Routine ?: TimeRoutinePageUiState.Routine.default()
            val routineComposition = domainResult.value
            val routineUuid = routineComposition.timeRoutine.uuid
            routineState.copy(
                title = routineComposition.timeRoutine.title,
                slotItemList = routineComposition.timeSlots.map { slotEntity: TimeSlotEntity ->
                    val slotItem = slotEntity.toSlotItem(routineUuid)
                    slotItem.splitOverMidnight()
                }.flatten(),
                dayOfWeeks = routineComposition.dayOfWeeks.map {
                    it.dayOfWeek
                }.sorted(),
                dayOfWeekName = value.currentDayOfWeek.getDisplayName(
                    TextStyle.SHORT,
                    Locale.getDefault()
                )
            )
        }

        null -> {
            TimeRoutinePageUiState.Loading.default()
        }
    }
}

private fun TimeSlotItemUiState.splitOverMidnight(): List<TimeSlotItemUiState> {
    return if (this.startMinutesOfDay > this.endMinutesOfDay) {
        //ex: 22 ~ 6

        //24 + 6(endTime)
        val overEndMinutes = LocalTimeUtil.DAY_MINUTES + (this.endMinutesOfDay)
        //00 - (24 - 22(startTime))
        val negativeStartMinutes =
            0 - (LocalTimeUtil.DAY_MINUTES - this.startMinutesOfDay)
        listOf(
            this.copy(
                startMinutesOfDay = this.startMinutesOfDay,
                endMinutesOfDay = overEndMinutes,
            ),
            this.copy(
                startMinutesOfDay = negativeStartMinutes,
                endMinutesOfDay = this.endMinutesOfDay,
            )
        )
    } else {
        listOf(
            this
        )
    }
}

private sealed interface UiPreState {
    data class Routine(
        val currentDayOfWeek: DayOfWeek,
        val routineDomainResult: DomainResult<TimeRoutineComposition>?,
    ) : UiPreState

    data class Intent(
        val intent: TimeRoutinePageUiIntent,
    ) : UiPreState

    data class UpdateSlotList(
        val timeSlotList: List<TimeSlotItemUiState>
    ) : UiPreState
}

private fun TimeSlotEntity.toSlotItem(routineUuid: String): TimeSlotItemUiState {
    return TimeSlotItemUiState(
        slotUuid = this.uuid,
        routineUuid = routineUuid,
        title = this.title,
        startMinutesOfDay = this.startTime.asMinutes(),
        endMinutesOfDay = this.endTime.asMinutes(),
        slotClickIntent = TimeRoutinePageUiIntent.ShowSlotEdit(
            this.uuid, routineUuid
        ),
        isSelected = false,
    )
}