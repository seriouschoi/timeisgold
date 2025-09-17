package software.seriouschoi.timeisgold.feature.timeroutine.pager

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import software.seriouschoi.navigator.DestNavigatorPort
import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.core.common.ui.asResultState
import software.seriouschoi.timeisgold.core.common.util.Envelope
import software.seriouschoi.timeisgold.core.domain.mapper.onlySuccess
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.WatchTimeRoutineDefinitionUseCase
import software.seriouschoi.timeisgold.feature.timeroutine.edit.TimeRoutineEditScreenRoute
import software.seriouschoi.timeisgold.feature.timeroutine.timeslot.edit.TimeSlotEditScreenRoute
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 8. 26.
 * jhchoi
 */
@HiltViewModel
internal class TimeRoutinePagerViewModel @Inject constructor(
    private val saved: SavedStateHandle,
    private val navigator: DestNavigatorPort,
    private val watchTimeRoutineDefinitionUseCase: WatchTimeRoutineDefinitionUseCase
) : ViewModel() {

    private val initPagerFlow = flow {
        val pagerItems = DAY_OF_WEEKS
        val today = DayOfWeek.from(LocalDate.now())
        val initialIndex = pagerItems.indexOf(today)
        emit(
            UiPreState.Init(
                pageItems = pagerItems,
                initialPageIndex = initialIndex
            )
        )
    }


    private val intentState = MutableSharedFlow<Envelope<TimeRoutinePagerUiIntent>>()

    @OptIn(FlowPreview::class)
    private val currentDayOfWeekFlow = intentState.mapNotNull {
        it.payload as? TimeRoutinePagerUiIntent.LoadRoutine
    }.mapNotNull {
        it.dayOfWeek
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = null
    ).debounce(200)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val pagerStateRoutineFlow =
        currentDayOfWeekFlow.mapNotNull { it }.flatMapLatest { dayOfWeek ->
            watchTimeRoutineDefinitionUseCase.invoke(dayOfWeek)
        }.asResultState().onlySuccess().stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )

    private val pagerUiPreStateFlow = combine(
        currentDayOfWeekFlow,
        pagerStateRoutineFlow
    ) { dayOfWeek, routine ->
        val routineTitle = routine?.timeRoutine?.title ?: ""
        val dayOfWeekName = dayOfWeek?.getDisplayName(
            TextStyle.SHORT, Locale.getDefault()
        ) ?: ""
        UiPreState.PagerState(
            title = UiText.Raw(routineTitle),
            dayOfWeekName = UiText.Raw(dayOfWeekName),
            showAddTimeSlotButton = routine != null
        )
    }

    val uiState: StateFlow<TimeRoutinePagerUiState> = merge(
        initPagerFlow,
        pagerUiPreStateFlow
    ).scan(TimeRoutinePagerUiState()) { acc, value ->
        when (value) {
            is UiPreState.Init -> acc.copy(
                pagerItems = value.pageItems,
                initialPageIndex = value.initialPageIndex
            )

            is UiPreState.PagerState -> {
                acc.copy(
                    title = value.title,
                    dayOfWeekName = value.dayOfWeekName,
                    showAddTimeSlotButton = value.showAddTimeSlotButton
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = TimeRoutinePagerUiState()
    )

    fun sendIntent(intent: TimeRoutinePagerUiIntent) {
        viewModelScope.launch {
            intentState.emit(Envelope(intent))
        }
    }


    private fun handleIntentSideEffect(intent: TimeRoutinePagerUiIntent) {
        when (intent) {
            TimeRoutinePagerUiIntent.ModifyRoutine -> {
                moveToRoutineEdit()
            }
            is TimeRoutinePagerUiIntent.AddRoutine -> {
                moveToTimeSlotEdit()
            }

            else -> {
                //no work.
            }
        }
    }

    private fun moveToTimeSlotEdit() {
        viewModelScope.launch {
            // TODO: show time slot edit screen.
            /*
            파라미터 보내야함. 루틴 uuid, slot uuid.
             */
            navigator.navigate(TimeSlotEditScreenRoute)
        }
    }

    private fun moveToRoutineEdit() {
        viewModelScope.launch {
            val currentDayOfWeek = currentDayOfWeekFlow.first() ?: return@launch

            val route = TimeRoutineEditScreenRoute(
                dayOfWeekOrdinal = currentDayOfWeek.ordinal
            )
            navigator.navigate(route)
        }
    }

    init {
        viewModelScope.launch {
            intentState.collect {
                handleIntentSideEffect(it.payload)
            }
        }
    }

    companion object {
        private val DAY_OF_WEEKS = listOf(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY,
            DayOfWeek.SUNDAY
        )
    }
}


private sealed interface UiPreState {
    data class Init(
        val pageItems: List<DayOfWeek>,
        val initialPageIndex: Int,
    ) : UiPreState

    data class PagerState(
        val title: UiText = UiText.Raw(""),
        val dayOfWeekName: UiText = UiText.Raw(""),
        val showAddTimeSlotButton: Boolean = false
    ) : UiPreState
}

