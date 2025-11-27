package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import software.seriouschoi.timeisgold.core.common.ui.ResultState
import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.core.common.ui.withResultStateLifecycle
import software.seriouschoi.timeisgold.core.common.util.LocalTimeUtil
import software.seriouschoi.timeisgold.core.common.util.MetaEnvelope
import software.seriouschoi.timeisgold.core.common.util.MetaInfo
import software.seriouschoi.timeisgold.core.common.util.asMinutes
import software.seriouschoi.timeisgold.core.domain.mapper.asDomainError
import software.seriouschoi.timeisgold.core.domain.mapper.asResultState
import software.seriouschoi.timeisgold.core.domain.mapper.toUiText
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.vo.TimeSlotVO
import software.seriouschoi.timeisgold.domain.usecase.timeslot.DeleteTimeSlotUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.SetTimeSlotListUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.SetTimeSlotUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.WatchTimeSlotListUseCase
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.TimeSlotListStateHolder
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.item.TimeSlotItemUiState
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.logic.TimeSlotCalculator
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.logic.TimeSlotChangeTimeType
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.slotedit.TimeSlotEditState
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.slotedit.TimeSlotEditStateHolder
import timber.log.Timber
import java.time.DayOfWeek
import java.time.LocalTime
import javax.inject.Inject
import software.seriouschoi.timeisgold.core.common.ui.R as CommonR

/**
 * Created by jhchoi on 2025. 8. 26.
 * jhchoi
 */
@HiltViewModel
internal class TimeSlotListPageViewModel @Inject constructor(
    private val watchTimeSlotListUseCase: WatchTimeSlotListUseCase,

    @Deprecated("use only swap. swap will deprecate")
    private val setTimeSlotsUseCase: SetTimeSlotListUseCase,
    private val setTimeSlotUseCase: SetTimeSlotUseCase,
    private val deleteTimeSlotUseCase: DeleteTimeSlotUseCase,

    private val timeSlotListStateHolder: TimeSlotListStateHolder,
    private val timeSlotEditStateHolder: TimeSlotEditStateHolder,

    private val timeSlotCalculator: TimeSlotCalculator,
) : ViewModel() {

    private val dayOfWeekFlow = MutableStateFlow<DayOfWeek?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val timeslotList: StateFlow<ResultState<List<MetaEnvelope<TimeSlotVO>>>> =
        dayOfWeekFlow.mapNotNull {
            it
        }.flatMapLatest {
            watchTimeSlotListUseCase.invoke(it)
        }.map {
            it.asResultState()
        }.withResultStateLifecycle().stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = ResultState.Loading
        )

    private val _intent = MutableSharedFlow<MetaEnvelope<TimeSlotListPageUiIntent>>()

    //state holder의 조합만 이뤄짐.
    val uiState = combine(
        timeSlotListStateHolder.state,
        timeSlotEditStateHolder.state,
    ) { listState, editState ->
        TimeSlotListPageUiState(
            slotListState = listState,
            editState = editState,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = TimeSlotListPageUiState()
    )

    private val _uiEvent: MutableSharedFlow<MetaEnvelope<TimeSlotListPageUiEvent>> =
        MutableSharedFlow()
    val uiEvent: SharedFlow<MetaEnvelope<TimeSlotListPageUiEvent>> = _uiEvent

    init {
        watchIntent()
        watchSlotList()
        watchTimeSlotEditState()
    }

    private fun watchSlotList() {
        timeslotList.onEach { resultState ->
            when (resultState) {
                is ResultState.Error -> {
                    timeSlotListStateHolder.showError(
                        resultState.asDomainError().toUiText()
                    )
                }

                is ResultState.Loading -> {
                    timeSlotListStateHolder.showLoading()
                }

                is ResultState.Success -> {
                    val itemList = resultState.data.map {
                        TimeSlotItemUiState(
                            slotUuid = it.metaInfo.uuid,
                            title = it.payload.title,
                            startMinutesOfDay = it.payload.startTime.asMinutes(),
                            endMinutesOfDay = it.payload.endTime.asMinutes(),
                        )
                    }
                    timeSlotListStateHolder.setList(itemList)
                }
            }
        }.launchIn(viewModelScope)
    }

    @OptIn(FlowPreview::class)
    private fun watchTimeSlotEditState() {
        combine(
            timeSlotEditStateHolder.state.mapNotNull { it },
            dayOfWeekFlow.mapNotNull { it }
        ) { state, dayOfWeek ->
            state to dayOfWeek
        }.distinctUntilChangedBy {
            it.first
        }.debounce(
            timeoutMillis = 500
        ).onEach { (state, week) ->
            Timber.d("change time slot edit.")
            applyTimeSlot(
                slotVO = TimeSlotVO(
                    startTime = state.startTime,
                    endTime = state.endTime,
                    title = state.title
                ),
                dayOfWeek = week,
                slotId = state.slotUuid
            )
        }.launchIn(viewModelScope)
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

    private suspend fun handleIntentSideEffect(intent: TimeSlotListPageUiIntent) {
        Timber.d("intent received. ${intent.javaClass.simpleName}")
        when (intent) {

            is TimeSlotListPageUiIntent.ApplyTimeSlotListChanges -> {
                applyTimeSlotList()
            }

            TimeSlotListPageUiIntent.SlotEditCancel -> {
                timeSlotEditStateHolder.clear()
            }


            is TimeSlotListPageUiIntent.SelectTimeSlice -> {
                val currentSlotList = timeSlotListStateHolder.state.first().slotItemList.map {
                    val startTime = LocalTimeUtil.create(it.startMinutesOfDay)
                    val endTime = LocalTimeUtil.create(it.endMinutesOfDay)
                    startTime to endTime
                }

                val availableTimeSlot = findAvailableTimeSlot(currentSlotList, intent.hourOfDay)
                if (availableTimeSlot != null) {
                    timeSlotEditStateHolder.show(
                        TimeSlotEditState(
                            slotUuid = null,
                            title = "",
                            startTime = availableTimeSlot.first,
                            endTime = availableTimeSlot.second
                        )
                    )
                }
            }

            is TimeSlotListPageUiIntent.SelectTimeSlot -> {
                timeSlotEditStateHolder.show(
                    TimeSlotEditState(
                        slotUuid = intent.slot.slotUuid,
                        title = intent.slot.title,
                        startTime = LocalTimeUtil.create(intent.slot.startMinutesOfDay),
                        endTime = LocalTimeUtil.create(intent.slot.endMinutesOfDay),
                    )
                )
            }

            is TimeSlotListPageUiIntent.DeleteTimeSlot -> {
                timeSlotEditStateHolder.clear()
                deleteTimeSlot(intent.slotId)
            }

            is TimeSlotListPageUiIntent.ChangeSelectedTimeSlotEndTime -> {
                timeSlotEditStateHolder.changeEndTime(intent.endTime)
            }

            is TimeSlotListPageUiIntent.ChangeSelectedTimeSlotStartTime -> {
                timeSlotEditStateHolder.changeStartTime(intent.startTime)
            }

            is TimeSlotListPageUiIntent.ChangeSelectedTimeSlotTitle -> {
                timeSlotEditStateHolder.changeTitle(intent.title)
            }

            is TimeSlotListPageUiIntent.DragTimeSlotBody -> {
                handleUpdateTimeSlotIntent(
                    intent.slotId, intent.minuteFactor,
                    TimeSlotChangeTimeType.START_AND_END
                )
            }

            is TimeSlotListPageUiIntent.DragTimeSlotFooter -> {
                handleUpdateTimeSlotIntent(
                    intent.slotId,
                    intent.minuteFactor,
                    TimeSlotChangeTimeType.END_TIME
                )
            }

            is TimeSlotListPageUiIntent.DragTimeSlotHeader -> {
                handleUpdateTimeSlotIntent(
                    intent.slotId,
                    intent.minuteFactor,
                    TimeSlotChangeTimeType.START_TIME
                )
            }
        }
    }

    private fun deleteTimeSlot(slotId: String) {
        flow {
            emit(deleteTimeSlotUseCase.invoke(slotId))
        }.map {
            it.asResultState()
        }.withResultStateLifecycle().launchIn(viewModelScope)
    }

    /**
     * @param existingSlots 현재 시간 슬롯 목록
     * @param startHourOfDay 시작 시간 (hour)
     * @return 시작 시간과 종료 시간 Pair
     */
    private fun findAvailableTimeSlot(
        existingSlots: List<Pair<LocalTime, LocalTime>>,
        startHourOfDay: Int,
    ): Pair<LocalTime, LocalTime>? {

        val startOfHour = startHourOfDay * 60
        val endOfHour = startOfHour + 60

        val slotsInMinutes = existingSlots
            .map { it.first.asMinutes() to it.second.asMinutes() }
            .sortedBy { it.first }

        var potentialStartTime = startOfHour

        for ((slotStart, slotEnd) in slotsInMinutes) {
            if (potentialStartTime >= slotEnd) continue

            if (potentialStartTime < slotStart) {
                val availableEnd = minOf(slotStart, endOfHour)
                if (potentialStartTime < availableEnd) {
                    return LocalTimeUtil.create(potentialStartTime) to LocalTimeUtil.create(
                        availableEnd
                    )
                }
            }
            potentialStartTime = maxOf(potentialStartTime, slotEnd)
        }

        if (potentialStartTime < endOfHour) {
            return LocalTimeUtil.create(potentialStartTime) to LocalTimeUtil.create(endOfHour)
        }

        return null
    }

    private suspend fun handleUpdateTimeSlotIntent(
        slotId: String,
        minuteFactor: Int,
        changeTimeType: TimeSlotChangeTimeType
    ) {
        val slotListState = timeSlotListStateHolder.state.first()
        val newList = timeSlotCalculator.adjustSlotList(
            slotMinuteFactor = minuteFactor,
            targetSlotId = slotId,
            currentList = slotListState.slotItemList,
            changeType = changeTimeType
        )
        timeSlotListStateHolder.setList(newList)

        // time slot edit이 표시 상태라면, 함께 갱신한다.
        // 현재 편집 중인 슬롯이 있는지 확인

        timeSlotEditStateHolder.state.first()?.slotUuid?.let { editingSlotUuid ->
            // 업데이트된 목록에서 현재 편집 중인 슬롯의 최신 정보를 찾음
            newList.find { it.slotUuid == editingSlotUuid }?.let { updatedSlot ->
                // TimeSlotEditStateHolder에 변경된 시간 정보로 업데이트 인텐트를 보냄
                timeSlotEditStateHolder.show(
                    TimeSlotEditState(
                        slotUuid = updatedSlot.slotUuid,
                        title = updatedSlot.title,
                        startTime = LocalTimeUtil.create(updatedSlot.startMinutesOfDay),
                        endTime = LocalTimeUtil.create(updatedSlot.endMinutesOfDay),
                    )
                )
            }
        }
    }

    private fun applyTimeSlot(
        dayOfWeek: DayOfWeek,
        slotVO: TimeSlotVO,
        slotId: String?,
    ) {
        flow {
            setTimeSlotUseCase.execute(
                dayOfWeek = dayOfWeek,
                timeSlot = slotVO,
                slotId = slotId
            ).asResultState().let { emit(it) }
        }.withResultStateLifecycle().onEach { state ->
            when (state) {
                is ResultState.Error -> {
                    // no work.
                }

                ResultState.Loading -> {
                    // no work.
                }

                is ResultState.Success -> {
                    val updatedId = state.data.uuid
                    timeSlotEditStateHolder.changeSlotId(updatedId)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun applyTimeSlotList() {
        val applyFlow = combine(
            timeSlotListStateHolder.state,
            dayOfWeekFlow.mapNotNull { it }
        ) { dataState, dayOfWeek ->
            val updateSlots = dataState.slotItemList.associate {
                it.slotUuid to TimeSlotVO(
                    startTime = LocalTimeUtil.create(it.startMinutesOfDay),
                    endTime = LocalTimeUtil.create(it.endMinutesOfDay),
                    title = it.title
                )
            }

            setTimeSlotsUseCase.invoke(
                dayOfWeek = dayOfWeek,
                timeSlotMap = updateSlots
            )
        }.map { it: DomainResult<List<MetaInfo>> ->
            it.asResultState()
        }.withResultStateLifecycle()

        val loading = applyFlow.filterIsInstance<ResultState.Loading>()
        val error = applyFlow.filterIsInstance<ResultState.Error>().mapNotNull {
            it.asDomainError()
        }
        val success = applyFlow.mapNotNull { (it as? ResultState.Success)?.data }.onEach {
            Timber.d("apply time slot list success.")
        }

        val eventFlow: Flow<MetaEnvelope<TimeSlotListPageUiEvent>> = merge(
            loading.map {
                null
            },
            error.map {
                TimeSlotListPageUiEvent.ShowToast(
                    it.toUiText(),
                    Toast.LENGTH_SHORT
                )
            },
            success.map {
                TimeSlotListPageUiEvent.ShowToast(
                    UiText.MultipleResArgs.create(
                        CommonR.string.message_format_complete,
                        CommonR.string.text_save,
                    ),
                    Toast.LENGTH_SHORT
                )
            }
        ).mapNotNull {
            it?.let { MetaEnvelope(it) }
        }

        eventFlow.onEach {
            _uiEvent.emit(it)
        }.launchIn(viewModelScope)
    }

    fun sendIntent(createRoutine: TimeSlotListPageUiIntent) {
        viewModelScope.launch {
            _intent.emit(MetaEnvelope(createRoutine))
        }
    }
}