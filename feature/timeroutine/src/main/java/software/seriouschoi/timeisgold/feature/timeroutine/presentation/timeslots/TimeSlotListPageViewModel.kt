package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots

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
import software.seriouschoi.timeisgold.core.domain.mapper.onlyDomainSuccess
import software.seriouschoi.timeisgold.core.domain.mapper.toUiText
import software.seriouschoi.timeisgold.domain.data.DomainError
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.data.entities.TimeSlotEntity
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.WatchTimeRoutineCompositionUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.NormalizeMinutesForUiUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.SetTimeSlotListUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.valid.GetTimeSlotPolicyValidUseCase
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.edit.slot.TimeSlotEditScreenRoute
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.TimeSlotItemUiState
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.midMinute
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.timeLog
import timber.log.Timber
import java.time.DayOfWeek
import javax.inject.Inject
import software.seriouschoi.timeisgold.core.common.ui.R as CommonR

/**
 * Created by jhchoi on 2025. 8. 26.
 * jhchoi
 */
@HiltViewModel
internal class TimeSlotListPageViewModel @Inject constructor(
    private val navigator: DestNavigatorPort,
    private val watchTimeRoutineCompositionUseCase: WatchTimeRoutineCompositionUseCase,
    private val setTimeSlotsUseCase: SetTimeSlotListUseCase,
    private val normalizeMinutesForUiUseCase: NormalizeMinutesForUiUseCase,
    private val getTimeSlotPolicyValidUseCase: GetTimeSlotPolicyValidUseCase,
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
            started = SharingStarted.Lazily,
            initialValue = ResultState.Loading
        )

    private val routinePreUiStateFlow =
        routineCompositionFlow.onlyDomainResult().mapNotNull { routineComposition ->
            UiPreState.Routine(
                routineDomainResult = routineComposition
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )


    private val _intent = MutableSharedFlow<Envelope<TimeRoutinePageUiIntent>>()

    private val _timeSlotUpdatePreUiStateFlow = MutableSharedFlow<UiPreState.UpdateSlotList>()

    val uiState: StateFlow<TimeSlotListPageUiState> = merge(
        routinePreUiStateFlow.mapNotNull { it },
        _timeSlotUpdatePreUiStateFlow
    ).scan(
        TimeSlotListPageUiState().loadingState()
    ) { acc: TimeSlotListPageUiState, value: UiPreState ->
        acc.reduce(value)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = TimeSlotListPageUiState().loadingState()
    )
    private val _uiEvent: MutableSharedFlow<Envelope<TimeSlotListPageUiEvent>> = MutableSharedFlow()
    val uiEvent: SharedFlow<Envelope<TimeSlotListPageUiEvent>> = _uiEvent

    init {
        watchIntent()
    }

    fun load(dayOfWeek: DayOfWeek) {
        viewModelScope.launch {
            dayOfWeekFlow.emit(dayOfWeek)
        }
    }

    private fun watchIntent() {
        _intent.onEach {
            handleIntentSideEffect(it.payload)
        }.launchIn(viewModelScope)
    }


    private suspend fun handleIntentSideEffect(intent: TimeRoutinePageUiIntent) {
        when (intent) {
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

    private var dragMinsAcc = 0
    private suspend fun handleUpdateTimeSlot(
        intent: TimeRoutinePageUiIntent.UpdateTimeSlotUi,
    ) {
        val dataState = uiState.value ?: return

        val currentSlotItemList = dataState.slotItemList
        val intentItems = currentSlotItemList.find { it.slotUuid == intent.uuid }?.let {
            val startMins = it.startMinutesOfDay
            val endMins = it.endMinutesOfDay

            val newStartMin = normalizeMinutesForUiUseCase.invoke(startMins + dragMinsAcc)
            val newEndMin = normalizeMinutesForUiUseCase.invoke(endMins + dragMinsAcc)
            if (startMins == newStartMin) {
                dragMinsAcc += intent.minuteFactor
            } else {
                dragMinsAcc = 0
            }
            when (intent.updateTimeType) {
                TimeSlotUpdateTimeType.START -> it.copy(startMinutesOfDay = newStartMin)
                TimeSlotUpdateTimeType.END -> it.copy(endMinutesOfDay = newEndMin)
                TimeSlotUpdateTimeType.START_AND_END -> {
                    it.copy(
                        startMinutesOfDay = newStartMin,
                        endMinutesOfDay = newEndMin
                    )
                }
            }
        } ?: return


        val updateResult = updateSlotListState(
            updateItem = intentItems,
            slotItemList = currentSlotItemList,
            useSwap = intent.updateTimeType == TimeSlotUpdateTimeType.START_AND_END
        )

        //연산이 완료되면, 음수 좌표, 초과분 좌표를 다시 정규범위로 넣은뒤, 다시 자정이 넘은 슬롯은 둘로 쪼갠다.
        val newUpdateList = updateResult.flatMap {
            it.copy(
                startMinutesOfDay = LocalTimeUtil.create(it.startMinutesOfDay).asMinutes(),
                endMinutesOfDay = LocalTimeUtil.create(it.endMinutesOfDay).asMinutes()
            ).splitOverMidnight().map { splitItem ->
                splitItem.copy(isSelected = splitItem.slotUuid == intentItems.slotUuid)
            }
        }.distinct()

        _timeSlotUpdatePreUiStateFlow.emit(
            UiPreState.UpdateSlotList(newUpdateList)
        )
    }

    private fun updateSlotListState(
        updateItem: TimeSlotItemUiState,
        slotItemList: List<TimeSlotItemUiState>,
        useSwap: Boolean
    ): List<TimeSlotItemUiState> {
        val policyResult = getTimeSlotPolicyValidUseCase.invoke(updateItem.asEntity())
        if (policyResult !is DomainResult.Success) {
            return slotItemList
        }

        //오버랩 있음.
        return if (useSwap) {
            //오버랩 아이템 순번 전환.
            slotItemList.swapSlotList(updateItem)
        } else {
            slotItemList.update(updateItem)
        }
    }

    private fun updateTimeSlotList() {
        flowResultState {
            val currentRoutine =
                routineCompositionFlow.onlyDomainSuccess().first()
                    ?: return@flowResultState DomainResult.Failure(DomainError.NotFound.TimeRoutine)

            val dataState = uiState.first()

            val updateSlots = dataState.slotItemList.map {
                it.asEntity()
            }

            setTimeSlotsUseCase.invoke(
                timeRoutineUuid = currentRoutine.timeRoutine.uuid,
                timeSlotList = updateSlots
            )
        }.onlyDomainResult().onEach { domainResult ->
            when (domainResult) {
                is DomainResult.Failure -> {
                    TimeSlotListPageUiEvent.ShowToast(
                        domainResult.error.toUiText(),
                        Toast.LENGTH_SHORT
                    )
                }

                is DomainResult.Success -> {
                    TimeSlotListPageUiEvent.ShowToast(
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

private fun TimeSlotItemUiState.asEntity() = TimeSlotEntity(
    uuid = this.slotUuid,
    title = this.title,
    startTime = LocalTimeUtil.create(this.startMinutesOfDay),
    endTime = LocalTimeUtil.create(this.endMinutesOfDay),
    createTime = System.currentTimeMillis()
    // TODO: jhchoi 2025. 10. 8. createTime을 여기서 주는것도 문제인데..
)

private fun TimeSlotListPageUiState.reduce(value: UiPreState): TimeSlotListPageUiState {
    return when (value) {
        is UiPreState.Routine -> {
            this.reduceFromRoutine(value)
        }

        is UiPreState.UpdateSlotList -> {
            this.copy(slotItemList = value.timeSlotList)
        }
    }
}


private fun TimeSlotListPageUiState.reduceFromRoutine(value: UiPreState.Routine): TimeSlotListPageUiState {
    val domainResult = value.routineDomainResult
    return when (domainResult) {
        is DomainResult.Failure -> {
            val newState = this.copy(
                loadingMessage = null,
            )
            when (domainResult.error) {
                is DomainError.NotFound -> {
                    //빈 슬롯 리턴.
                    newState.copy(
                        errorState = null,
                        slotItemList = emptyList()
                    )
                }

                else -> {
                    val errorState = TimeSlotListPageErrorState(
                        errorMessage = domainResult.error.toUiText(),
                    )
                    newState.copy(
                        errorState = errorState
                    )
                }
            }
        }

        is DomainResult.Success -> {
            val newState = this.copy(
                loadingMessage = null,
                errorState = null
            )
            val routineComposition = domainResult.value
            val routineUuid = routineComposition.timeRoutine.uuid
            newState.copy(
                slotItemList = routineComposition.timeSlots.flatMap { slotEntity: TimeSlotEntity ->
                    val slotItem = slotEntity.toSlotItem(routineUuid)
                    slotItem.splitOverMidnight()
                },
            )
        }

        null -> {
            this.copy(
                loadingMessage = UiText.MultipleResArgs.create(
                    CommonR.string.message_format_loading,
                    CommonR.string.text_routine
                )
            )
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
        val routineDomainResult: DomainResult<TimeRoutineComposition>?,
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
        isSelected = false,
    )
}

private fun List<TimeSlotItemUiState>.swapSlotList(
    updateItem: TimeSlotItemUiState,
): List<TimeSlotItemUiState> {

    val overlapItem = this.getOverlapItem(updateItem)
    if (overlapItem == null) {
        return this.update(updateItem)
    }

    val updateSourceTime: TimeSlotItemUiState = this.find {
        updateItem.slotUuid == it.slotUuid
    } ?: return this
    val updateItemMinutes = updateItem.run { this.endMinutesOfDay - this.startMinutesOfDay }

    val overlapItemMinutes =
        overlapItem.run { this.endMinutesOfDay - this.startMinutesOfDay }

    val newUpdateItem: TimeSlotItemUiState
    val newOverlapItem: TimeSlotItemUiState
    when {
        updateSourceTime.midMinute() > updateItem.midMinute() -> {
            //down to up.
            Timber.d("down to up.")
            newUpdateItem = updateSourceTime.copy(
                startMinutesOfDay = overlapItem.startMinutesOfDay,
                endMinutesOfDay = overlapItem.startMinutesOfDay + updateItemMinutes
            )
            newOverlapItem = overlapItem.copy(
                startMinutesOfDay = updateSourceTime.endMinutesOfDay - overlapItemMinutes,
                endMinutesOfDay = updateSourceTime.endMinutesOfDay,
            )
        }

        updateSourceTime.midMinute() < updateItem.midMinute() -> {
            //up to down
            Timber.d("up to down.")
            newUpdateItem = updateSourceTime.copy(
                startMinutesOfDay = overlapItem.endMinutesOfDay - updateItemMinutes,
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

    return this.map {
        when (it.slotUuid) {
            newOverlapItem.slotUuid -> newOverlapItem
            newUpdateItem.slotUuid -> newUpdateItem
            else -> it
        }
    }
}

private fun List<TimeSlotItemUiState>.getOverlapItem(
    updateItem: TimeSlotItemUiState,
): TimeSlotItemUiState? = find {
    if (updateItem.slotUuid == it.slotUuid) return@find false
    else {
        LocalTimeUtil.overlab(
            updateItem.startMinutesOfDay % LocalTimeUtil.DAY_MINUTES until updateItem.endMinutesOfDay % LocalTimeUtil.DAY_MINUTES,
            it.startMinutesOfDay % LocalTimeUtil.DAY_MINUTES until it.endMinutesOfDay % LocalTimeUtil.DAY_MINUTES
        )
    }
}

private fun List<TimeSlotItemUiState>.update(
    updateItem: TimeSlotItemUiState,
): List<TimeSlotItemUiState> {
    val overlapItem = this.getOverlapItem(updateItem)

    if (overlapItem == null) {
        //중복 없음. 업데이트.
        return this.map {
            if (it.slotUuid == updateItem.slotUuid) updateItem
            else it
        }
    }

    val updateOriginItem = this.find { it.slotUuid == updateItem.slotUuid } ?: return this

    //update와 updateOirigin의 중앙값을 비교하여, 진행 방향 확인.
    val adjustedItem = when {
        updateItem.midMinute() < updateOriginItem.midMinute() -> {
            //아래에서 위로.
            // overlap의 아래쪽 끝에 닿지 않도록, overlap 바로 뒤로 이동
            updateItem.copy(
                startMinutesOfDay = overlapItem.endMinutesOfDay,
            )
        }

        // update가 overlap 위쪽(즉 더 이른 시간대)에 위치
        updateItem.midMinute() > updateOriginItem.midMinute() -> {
            // overlap의 위쪽 끝에 닿지 않도록, overlap 바로 위로 이동
            updateItem.copy(
                endMinutesOfDay = overlapItem.startMinutesOfDay
            )
        }

        // 완전히 겹침 (update가 overlap을 완전히 덮음)
        else -> {
            //오류이므로, 그냥 update무시.
            null
        }
    } ?: return this


    return this.map {
        if (it.slotUuid == adjustedItem.slotUuid) adjustedItem
        else it
    }
}