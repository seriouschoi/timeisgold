package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import software.seriouschoi.navigator.DestNavigatorPort
import software.seriouschoi.timeisgold.core.common.ui.ResultState
import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.core.common.ui.asResultState
import software.seriouschoi.timeisgold.core.common.ui.flowResultState
import software.seriouschoi.timeisgold.core.common.util.LocalTimeUtil
import software.seriouschoi.timeisgold.core.common.util.MetaEnvelope
import software.seriouschoi.timeisgold.core.common.util.MetaInfo
import software.seriouschoi.timeisgold.core.common.util.asMinutes
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
        timeslotList.map { resultState ->
            when (resultState) {
                is ResultState.Loading -> TimeSlotListStateIntent.Loading
                is ResultState.Error -> TimeSlotListStateIntent.Error(
                    UiText.Res(CommonR.string.message_error_tech_unknown)
                )

                is ResultState.Success -> {
                    when (val domainResult = resultState.data) {
                        is DomainResult.Failure -> {
                            when (val domainError = domainResult.error) {
                                is DomainError.NotFound -> TimeSlotListStateIntent.UpdateList(
                                    itemList = emptyList()
                                )

                                else -> TimeSlotListStateIntent.Error(domainError.toUiText())
                            }
                        }

                        is DomainResult.Success -> {
                            val slotList = domainResult.value.map {
                                TimeSlotItemUiState(
                                    slotUuid = it.metaInfo.uuid,
                                    title = it.payload.title,
                                    startMinutesOfDay = it.payload.startTime.asMinutes(),
                                    endMinutesOfDay = it.payload.endTime.asMinutes(),
                                    isSelected = false,
                                )
                            }
                            // TODO: 상태 처리에 대한 고민.
                            /*
                            slotList의 success만 watch해서 목록 stateHolder를 갱신하고,
                            여러 상태를 fail만 watch해서 오류를 state를 표시하고,
                            여러 상태의 loading만 watch해서 로딩을 보여주는 방법도 괜찮을려나..?

                            이를 위해선, uiState의 내부를 좀더 세분화 해야할 수도 있겠네..
                            스크린 데이터,
                            스크린 오류,
                            스크린 로딩,

                            하단 항목 편집 창.
                            편집창 오류.
                            편집창 로딩.

                            근데 그렇게 만들면, 속성이 너무 복잡해지는것 같은데..
                            현재 스크린 상태에 데이터, 오류, 로딩 속성을 두고 있는데..
                            이걸...차라리 오류가 일어날 상태를 수신하고 있다가..
                            상태 홀더에 오류라고 던지는 구현이 더 낫겠는데..



                             */
                            Timber.d("")
                            TimeSlotListStateIntent.UpdateList(slotList)
                        }
                    }
                }
            }
        }.onEach {
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
                    TimeSlotEditStateIntent.Update(
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
        flowResultState {
            Timber.d("update time slot. state=${slotVO}")

            setTimeSlotUseCase.execute(
                dayOfWeek = dayOfWeek,
                timeSlot = slotVO,
                slotId = slotId
            )
        }.map { resultState: ResultState<DomainResult<MetaInfo>> ->
            resultState.onlyDomainResult()
        }.onEach { domainResult ->
            when (domainResult) {
                is DomainResult.Failure -> {
                    Timber.d("update failed. ${domainResult.error}")
                }

                is DomainResult.Success -> {
                    Timber.d("update success. updated slotId=${domainResult.value.uuid}")
                    timeSlotEditStateHolder.sendIntent(
                        TimeSlotEditStateIntent.Update(
                            slotId = domainResult.value.uuid,
                        )
                    )
                }

                null -> {
                    //no work.
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun applyTimeSlotList() {
        flowResultState {
            val dayOfWeek = dayOfWeekFlow.first()
                ?: return@flowResultState DomainResult.Failure(DomainError.NotFound.TimeRoutine)


            val dataState = timeSlotListStateHolder.state.first()
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
                _uiEvent.emit(MetaEnvelope(it))
            }
        }.launchIn(viewModelScope)
    }

    fun sendIntent(createRoutine: TimeSlotListPageUiIntent) {
        viewModelScope.launch {
            _intent.emit(MetaEnvelope(createRoutine))
        }
    }
}