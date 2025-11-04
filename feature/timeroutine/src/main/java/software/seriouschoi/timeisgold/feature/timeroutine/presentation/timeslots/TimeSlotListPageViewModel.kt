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
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import software.seriouschoi.navigator.DestNavigatorPort
import software.seriouschoi.timeisgold.core.common.ui.ResultState
import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.core.common.ui.asResultState
import software.seriouschoi.timeisgold.core.common.ui.flowResultState
import software.seriouschoi.timeisgold.core.common.ui.withResultStateLifecycle
import software.seriouschoi.timeisgold.core.common.util.LocalTimeUtil
import software.seriouschoi.timeisgold.core.common.util.MetaEnvelope
import software.seriouschoi.timeisgold.core.common.util.MetaInfo
import software.seriouschoi.timeisgold.core.common.util.asMinutes
import software.seriouschoi.timeisgold.core.domain.mapper.asDomainError
import software.seriouschoi.timeisgold.core.domain.mapper.asResultState
import software.seriouschoi.timeisgold.core.domain.mapper.onlyDomainResult
import software.seriouschoi.timeisgold.core.domain.mapper.toUiText
import software.seriouschoi.timeisgold.domain.data.DomainError
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.vo.TimeSlotVO
import software.seriouschoi.timeisgold.domain.usecase.timeslot.DeleteTimeSlotUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.SetTimeSlotListUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.SetTimeSlotUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.WatchTimeSlotListUseCase
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.TimeSlotListStateHolder
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.TimeSlotListStateIntent
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.item.TimeSlotItemUiState
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.logic.TimeSlotCalculator
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.slotedit.TimeSlotEditState
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.slotedit.TimeSlotEditStateHolder
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.slotedit.TimeSlotEditStateIntent
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.slotedit.TimeSlotEditStateIntent.Init
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
    private val navigator: DestNavigatorPort,

    private val watchTimeSlotListUseCase: WatchTimeSlotListUseCase,
    private val setTimeSlotsUseCase: SetTimeSlotListUseCase,
    private val setTimeSlotUseCase: SetTimeSlotUseCase,
    private val deleteTimeSlotUseCase: DeleteTimeSlotUseCase,

    private val timeSlotListStateHolder: TimeSlotListStateHolder,
    private val timeSlotEditStateHolder: TimeSlotEditStateHolder,
    private val timeSlotCalculator: TimeSlotCalculator,
) : ViewModel() {

    private val dayOfWeekFlow = MutableStateFlow<DayOfWeek?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val timeslotList = dayOfWeekFlow.mapNotNull {
        it
    }.flatMapLatest {
        watchTimeSlotListUseCase.invoke(it)
    }.onEach {
        Timber.d("received time slot list.")
    }.asResultState().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = ResultState.Loading
    )

    private val _intent = MutableSharedFlow<MetaEnvelope<TimeSlotListPageUiIntent>>()

    //state holder의 조합만 이뤄짐.
    val uiState = combine(
        timeSlotListStateHolder.state,
        timeSlotEditStateHolder.state
    ) { listState, editState ->
        TimeSlotListPageUiState(
            slotListState = listState,
            editState = editState
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
        val loadingFlow: Flow<TimeSlotListStateIntent> =
            timeslotList.filterIsInstance<ResultState.Loading>().map {
                TimeSlotListStateIntent.Loading
            }
        val errorFlow: Flow<TimeSlotListStateIntent> =
            timeslotList.filterIsInstance<ResultState.Error>().map {
                TimeSlotListStateIntent.Error(
                    UiText.Res(CommonR.string.message_error_tech_unknown)
                )
            }

        val domainResultFlow = timeslotList.mapNotNull {
            it as? ResultState.Success
        }.mapNotNull {
            it.data
        }.shareIn(viewModelScope, SharingStarted.Lazily, replay = 1)

        val domainFailFlow = domainResultFlow.mapNotNull {
            it as? DomainResult.Failure
        }

        val emptyListFlow: Flow<TimeSlotListStateIntent> = domainFailFlow.mapNotNull {
            it.error as? DomainError.NotFound
        }.map {
            TimeSlotListStateIntent.UpdateList(
                itemList = emptyList()
            )
        }

        val domainErrorFlow: Flow<TimeSlotListStateIntent> = domainFailFlow.mapNotNull {
            it.error.takeIf { it !is DomainError.NotFound }?.let {
                TimeSlotListStateIntent.Error(it.toUiText())
            }
        }

        val domainSuccessFlow: Flow<TimeSlotListStateIntent> = domainResultFlow.mapNotNull {
            it as? DomainResult.Success
        }.map { domainResult ->
            val slotList = domainResult.value.map {
                TimeSlotItemUiState(
                    slotUuid = it.metaInfo.uuid,
                    title = it.payload.title,
                    startMinutesOfDay = it.payload.startTime.asMinutes(),
                    endMinutesOfDay = it.payload.endTime.asMinutes(),
                    isSelected = false,
                )
            }
            TimeSlotListStateIntent.UpdateList(slotList)
        }

        merge(
            loadingFlow,
            errorFlow,
            emptyListFlow,
            domainErrorFlow,
            domainSuccessFlow
        ).onEach {
            timeSlotListStateHolder.sendIntent(it)
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
            // TODO: 상태를 관찰하지 말고, 의도가 발행될때 apply를 수행하자.
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
        // TODO: jhchoi 2025. 11. 4. 이부분을 reducer/intentHandler로 만들어야 함.
        /*
        근데 reducer로 만들려면, intent의 결과를 또 받아서 어떻게 해야하는거지..?
         */
        when (intent) {

            is TimeSlotListPageUiIntent.ApplyTimeSlotListChanges -> {
                applyTimeSlotList()
            }

            is TimeSlotListPageUiIntent.UpdateTimeSlotUi -> {
                handleUpdateTimeSlotIntent(intent)
            }

            TimeSlotListPageUiIntent.Cancel -> {
                timeSlotEditStateHolder.sendIntent(TimeSlotEditStateIntent.Clear)
            }

            is TimeSlotListPageUiIntent.UpdateTimeSlotEdit -> {
                timeSlotEditStateHolder.sendIntent(
                    intent.slotEditState
                )
            }

            is TimeSlotListPageUiIntent.SelectTimeSlice -> {
                val currentSlotList = timeSlotListStateHolder.state.first().slotItemList.map {
                    val startTime = LocalTimeUtil.create(it.startMinutesOfDay)
                    val endTime = LocalTimeUtil.create(it.endMinutesOfDay)
                    startTime to endTime
                }

                val availableTimeSlot = findAvailableTimeSlot(currentSlotList, intent.hourOfDay)
                if (availableTimeSlot != null) {
                    timeSlotEditStateHolder.sendIntent(
                        Init(
                            state = TimeSlotEditState(
                                slotUuid = null,
                                title = "",
                                startTime = availableTimeSlot.first,
                                endTime = availableTimeSlot.second,
                            )
                        )
                    )
                }
            }

            is TimeSlotListPageUiIntent.SelectTimeSlot -> {
                timeSlotEditStateHolder.sendIntent(
                    Init(
                        state = TimeSlotEditState(
                            slotUuid = intent.slot.slotUuid,
                            title = intent.slot.title,
                            startTime = LocalTimeUtil.create(intent.slot.startMinutesOfDay),
                            endTime = LocalTimeUtil.create(intent.slot.endMinutesOfDay),
                        )
                    )
                )
            }

            is TimeSlotListPageUiIntent.DeleteTimeSlot -> {
                timeSlotEditStateHolder.sendIntent(TimeSlotEditStateIntent.Clear)
                deleteTimeSlot(intent.slotId)
            }
        }
    }

    private fun deleteTimeSlot(slotId: String) {
        flowResultState {
            deleteTimeSlotUseCase.invoke(slotId)
        }.launchIn(viewModelScope)
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

    private var dragMinsAcc = 0

    private suspend fun handleUpdateTimeSlotIntent(
        intent: TimeSlotListPageUiIntent.UpdateTimeSlotUi,
    ) {
        val slotListState = timeSlotListStateHolder.state.first()
        val (newList, nextAcc) = timeSlotCalculator.adjustSlotList(
            intent = intent,
            currentList = slotListState.slotItemList,
            dragAcc = dragMinsAcc
        )
        dragMinsAcc = nextAcc
        timeSlotListStateHolder.sendIntent(
            TimeSlotListStateIntent.UpdateList(newList)
        )

        // time slot edit이 표시 상태라면, 함께 갱신한다.
        // 현재 편집 중인 슬롯이 있는지 확인

        timeSlotEditStateHolder.state.first()?.slotUuid?.let { editingSlotUuid ->
            // 업데이트된 목록에서 현재 편집 중인 슬롯의 최신 정보를 찾음
            newList.find { it.slotUuid == editingSlotUuid }?.let { updatedSlot ->
                // TimeSlotEditStateHolder에 변경된 시간 정보로 업데이트 인텐트를 보냄
                timeSlotEditStateHolder.sendIntent(
                    intent = TimeSlotEditStateIntent.Update(
                        slotId = updatedSlot.slotUuid,
                        slotTitle = updatedSlot.title,
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
        val applyFlow = flow {
            setTimeSlotUseCase.execute(
                dayOfWeek = dayOfWeek,
                timeSlot = slotVO,
                slotId = slotId
            ).let {
                emit(it)
            }
        }.map { it.asResultState() }.withResultStateLifecycle()


        val loading = applyFlow.filterIsInstance<ResultState.Loading>()
        val failed = applyFlow.filterIsInstance<ResultState.Error>().map { it.asDomainError() }
        val success = applyFlow.mapNotNull { it as? ResultState.Success }

        merge(
            loading.mapNotNull {
                //no work.
                null
           },
            failed.mapNotNull {
                //no work.
                null
            },
            success.mapNotNull {
                //uuid 갱신.
                TimeSlotEditStateIntent.Update(
                    slotId = it.data.uuid,
                )
            }
        ).onEach {
            timeSlotEditStateHolder.sendIntent(
                it
            )
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
        val success = applyFlow.mapNotNull { (it as? ResultState.Success)?.data }

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