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
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.WatchRoutineUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.SetTimeSlotListUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.SetTimeSlotUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.WatchTimeSlotListUseCase
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.TimeSlotListStateHolder
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.TimeSlotListStateIntent
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.item.TimeSlotItemUiState
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.logic.TimeSlotCalculator
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.slotedit.TimeSlotEditState
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.slotedit.TimeSlotEditStateHolder
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.slotedit.TimeSlotEditStateIntent.Init
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.slotedit.TimeSlotEditStateIntent.Update
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

    private val watchRoutineUseCase: WatchRoutineUseCase,
    private val watchTimeSlotListUseCase: WatchTimeSlotListUseCase,
    private val setTimeSlotsUseCase: SetTimeSlotListUseCase,
    private val setTimeSlotUseCase: SetTimeSlotUseCase,

    private val timeSlotListStateHolder: TimeSlotListStateHolder,
    private val timeSlotEditStateHolder: TimeSlotEditStateHolder,
    private val timeSlotCalculator: TimeSlotCalculator,
) : ViewModel() {

    private val dayOfWeekFlow = MutableStateFlow<DayOfWeek?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val routine = dayOfWeekFlow.mapNotNull {
        it
    }.flatMapLatest {
        watchRoutineUseCase.invoke(it)
    }.onEach {
        Timber.d("received routine.")
    }.asResultState().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = ResultState.Loading
    )

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

    private val _uiEvent: MutableSharedFlow<MetaEnvelope<TimeSlotListPageUiEvent>> = MutableSharedFlow()
    val uiEvent: SharedFlow<MetaEnvelope<TimeSlotListPageUiEvent>> = _uiEvent

    init {
        watchIntent()
        watchSlotList()
        watchTimeSlotEdit()
    }

    private fun watchSlotList() {
        timeslotList.map {resultState ->
            when(resultState) {
                is ResultState.Loading -> TimeSlotListStateIntent.Loading
                is ResultState.Error -> TimeSlotListStateIntent.Error(
                    UiText.Res(CommonR.string.message_error_tech_unknown)
                )
                is ResultState.Success -> {
                    when(val domainResult = resultState.data) {
                        is DomainResult.Failure -> {
                            when(val domainError = domainResult.error) {
                                is DomainError.NotFound -> {
                                    TimeSlotListStateIntent.UpdateList(
                                        emptyList = emptyList()
                                    )
                                }
                                else -> {
                                    val errorMessage = domainError.toUiText()
                                    TimeSlotListStateIntent.Error(errorMessage)
                                }
                            }
                        }
                        is DomainResult.Success -> {
                            val slotList = domainResult.value.map {
                                TimeSlotItemUiState(
                                    slotUuid = it.metaInfo.uuid,
                                    title = it.payload.title,
                                    startMinutesOfDay = it.payload.startTime.asMinutes(),
                                    endMinutesOfDay = it.payload.endTime.asMinutes(),
                                    isSelected = false
                                )
                            }
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
    private fun watchTimeSlotEdit() {
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
            updateTimeSlotEdit(
                state = state,
                dayOfWeek = week
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

            is TimeSlotListPageUiIntent.UpdateTimeSlotList -> {
                updateTimeSlotList()
            }

            is TimeSlotListPageUiIntent.UpdateTimeSlotUi -> {
                handleUpdateTimeSlot(intent)
            }

            TimeSlotListPageUiIntent.Cancel -> {
                timeSlotEditStateHolder.sendIntent(Init(null))
            }

            is TimeSlotListPageUiIntent.UpdateTimeSlotEdit -> {
                timeSlotEditStateHolder.sendIntent(
                    intent.slotEditState
                )
            }

            is TimeSlotListPageUiIntent.SelectTimeSlice -> {
                timeSlotEditStateHolder.sendIntent(
                    Init(
                        state = TimeSlotEditState(
                            slotUuid = null,
                            title = "",
                            startTime = LocalTime.of(intent.hourOfDay, 0),
                            endTime = LocalTime.of(intent.hourOfDay, 0),
                        )
                    )
                )
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
        }
    }


    private var dragMinsAcc = 0

    private suspend fun handleUpdateTimeSlot(
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
    }

    private fun updateTimeSlotEdit(state: TimeSlotEditState, dayOfWeek: DayOfWeek) {
        flowResultState {
            Timber.d("update time slot. state=${state}")

            setTimeSlotUseCase.execute(
                dayOfWeek = dayOfWeek,
                timeSlot = TimeSlotVO(
                    startTime = state.startTime,
                    endTime = state.endTime,
                    title = state.title,
                ),
                slotId = state.slotUuid
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
                        Update(
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

    private fun updateTimeSlotList() {
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