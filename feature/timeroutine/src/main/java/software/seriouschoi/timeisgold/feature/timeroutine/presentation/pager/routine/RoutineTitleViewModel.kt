package software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager.routine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import software.seriouschoi.timeisgold.core.common.ui.ResultState
import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.core.common.ui.asResultState
import software.seriouschoi.timeisgold.core.common.ui.flowResultState
import software.seriouschoi.timeisgold.core.common.util.Envelope
import software.seriouschoi.timeisgold.core.domain.mapper.onlyDomainResult
import software.seriouschoi.timeisgold.core.domain.mapper.onlyResultSuccess
import software.seriouschoi.timeisgold.core.domain.mapper.toUiText
import software.seriouschoi.timeisgold.domain.data.DomainError
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineDefinition
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.SetRoutineTitleUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.WatchTimeRoutineDefinitionUseCase
import software.seriouschoi.timeisgold.feature.timeroutine.data.TimeRoutineFeatureState
import timber.log.Timber
import java.time.DayOfWeek
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 10. 9.
 * jhchoi
 */
@HiltViewModel
internal class RoutineTitleViewModel @Inject constructor(
    private val state: TimeRoutineFeatureState,
    private val watchTimeRoutineUseCase: WatchTimeRoutineDefinitionUseCase,
    private val setRoutineTitleUseCase: SetRoutineTitleUseCase,
) : ViewModel() {
    private val intent = MutableSharedFlow<Envelope<RoutineTitleIntent>>()

    @OptIn(FlowPreview::class)
    private val updateTitleIntent = intent.mapNotNull {
        it.payload as? RoutineTitleIntent.EditTitle
    }.debounce(500L).shareIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        replay = 0
    )

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

    private val errorUiPreState = MutableSharedFlow<UiPreState.ShowError>()

    val uiState = merge(
        routineDefinition.map {
            UiPreState.RoutineDefinition(it)
        },
        errorUiPreState,
        intent.map {
            UiPreState.Intent(it)
        }
    ).scan(
        RoutineTitleUiState()
    ) { acc, value: UiPreState ->
        acc.reduceFrom(value)
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        RoutineTitleUiState()
    )


    init {
        viewModelScope.launch {
            updateTitleIntent.collect {
                val dayOfWeek = currentDayOfWeek.onlyResultSuccess().first()
                updateTitle(it.title, dayOfWeek)
            }
        }
    }

    fun sendIntent(intent: RoutineTitleIntent) {
        viewModelScope.launch {
            this@RoutineTitleViewModel.intent.emit(Envelope(intent))
        }
    }

    private fun updateTitle(
        title: String,
        dayOfWeek: DayOfWeek?,
    ) {
        flowResultState {
            dayOfWeek
                ?: return@flowResultState DomainResult.Failure(DomainError.NotFound.TimeRoutine)

            setRoutineTitleUseCase.invoke(title, dayOfWeek)
        }.map {
            it.onlyDomainResult()
        }.onEach { result ->
            when (result) {
                is DomainResult.Failure -> {
                    val errorMessage = result.error.toUiText()
                    val errorState = UiPreState.ShowError(errorMessage)
                    errorUiPreState.emit(errorState)
                }

                is DomainResult.Success -> {
                    Timber.d("updateTitle. success")
                    //no work.
                }

                null -> {
                    //no work.
                }
            }
        }.launchIn(viewModelScope)
    }
}

private fun RoutineTitleUiState.reduceFrom(value: UiPreState): RoutineTitleUiState =
    when (value) {
        is UiPreState.RoutineDefinition -> {
            this.reduceFromDefinition(value)
        }

        is UiPreState.ShowError -> {
            this.reduceFromError(value)
        }

        is UiPreState.Intent -> {
            this.reduceFromIntent(value)
        }
    }

private fun RoutineTitleUiState.reduceFromIntent(value: UiPreState.Intent): RoutineTitleUiState {
    return when (val payload = value.intent.payload) {
        is RoutineTitleIntent.EditTitle -> {
            this.copy(
                title = payload.title
            )
        }
    }
}

private fun RoutineTitleUiState.reduceFromError(value: UiPreState.ShowError): RoutineTitleUiState =
    this.copy(
        loading = false,
        error = value.message
    )


private fun RoutineTitleUiState.reduceFromDefinition(value: UiPreState.RoutineDefinition): RoutineTitleUiState {
    return when (value.state) {
        is ResultState.Loading -> {
            this.copy(
                loading = true
            )
        }

        else -> {
            val newState = this.copy(loading = false, title = "")
            when (val domainResult = value.state.onlyDomainResult()) {
                is DomainResult.Failure -> {
                    val error = if (domainResult.error !is DomainError.NotFound) {
                        domainResult.error.toUiText()
                    } else {
                        null
                    }
                    newState.copy(
                        error = error,
                    )
                }

                is DomainResult.Success -> {
                    newState.copy(
                        title = domainResult.value.timeRoutine.title,
                    )
                }

                null -> {
                    newState
                }
            }
        }
    }
}

private sealed interface UiPreState {
    data class RoutineDefinition(
        val state: ResultState<DomainResult<TimeRoutineDefinition>>,
    ) : UiPreState

    data class ShowError(
        val message: UiText,
    ) : UiPreState

    data class Intent(val intent: Envelope<RoutineTitleIntent>) : UiPreState
}