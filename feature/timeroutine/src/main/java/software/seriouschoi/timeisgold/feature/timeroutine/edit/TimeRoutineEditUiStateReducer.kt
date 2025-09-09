package software.seriouschoi.timeisgold.feature.timeroutine.edit

import software.seriouschoi.timeisgold.core.common.ui.ResultState
import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.domain.data.DomainError
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineDefinition
import timber.log.Timber
import java.time.DayOfWeek
import software.seriouschoi.timeisgold.core.common.ui.R as CommonR

/**
 * Created by jhchoi on 2025. 9. 9.
 * jhchoi
 */
internal fun TimeRoutineEditUiState.reduceRoutineDomainResult(
    data: DomainResult<TimeRoutineDefinition>,
    defaultDayOfWeek: DayOfWeek,
): TimeRoutineEditUiState {
    Timber.d("createRoutineState data=$data")
    val routineState = TimeRoutineEditUiState.Routine(
        currentDayOfWeek = defaultDayOfWeek,
        dayOfWeekList = setOf(defaultDayOfWeek),
    )
    when (data) {
        is DomainResult.Failure -> {
            val error = data.error
            return when (error) {
                is DomainError.NotFound -> routineState
                else -> this
            }
        }

        is DomainResult.Success -> {
            val domainResult: TimeRoutineDefinition = data.value
            return routineState.copy(
                visibleDelete = true
            ).reduceRoutineDefinition(domainResult)
        }
    }
}

internal fun TimeRoutineEditUiState.Routine.reduceRoutineDefinition(
    routineComposition: TimeRoutineDefinition,
): TimeRoutineEditUiState {
    val newDayOfWeekList = routineComposition.dayOfWeeks.map {
        it.dayOfWeek
    }
    return this.copy(
        dayOfWeekList = listOf(
            newDayOfWeekList,
            this.dayOfWeekList
        ).flatten().toSet(),
        routineTitle = routineComposition.timeRoutine.title,
    )
}

internal fun TimeRoutineEditUiState.reduceIntent(
    intent: TimeRoutineEditUiIntent,
): TimeRoutineEditUiState {
    return when (intent) {
        is TimeRoutineEditUiIntent.UpdateDayOfWeek -> this.reduceIntentDayOfWeek(intent)
        is TimeRoutineEditUiIntent.UpdateRoutineTitle -> this.reduceIntentTitle(intent)
        else -> this
    }
}

internal fun TimeRoutineEditUiState.reduceIntentDayOfWeek(
    intent: TimeRoutineEditUiIntent.UpdateDayOfWeek,
): TimeRoutineEditUiState {
    val routineState = this as? TimeRoutineEditUiState.Routine ?: return this

    val newDayOfWeeks = routineState.dayOfWeekList.toMutableSet()
    if (intent.checked) {
        newDayOfWeeks.add(intent.dayOfWeek)
    } else {
        newDayOfWeeks.remove(intent.dayOfWeek)
    }
    return routineState.copy(
        dayOfWeekList = newDayOfWeeks
    )
}

internal fun TimeRoutineEditUiState.reduceIntentTitle(
    intent: TimeRoutineEditUiIntent.UpdateRoutineTitle,
): TimeRoutineEditUiState {
    val currentRoutineState = (this as? TimeRoutineEditUiState.Routine)
    return currentRoutineState?.copy(
        routineTitle = intent.title
    ) ?: this
}

internal fun TimeRoutineEditUiState.reduceValidResultState(
    validResult: ResultState<DomainResult<Boolean>>,
): TimeRoutineEditUiState {
    if (this !is TimeRoutineEditUiState.Routine) return this

    return when (validResult) {
        is ResultState.Success -> {
            val domainResult = validResult.data
            val newState = this.validState.reduceValidDomainResult(domainResult)
            this.copy(validState = newState)
        }

        is ResultState.Error -> {
            Timber.d("reduceValidResultState error. ${validResult.throwable.message}")
            val newState = this.validState.reduceValidResultState(validResult)
            this.copy(validState = newState)
        }

        ResultState.Loading -> this
    }
}

internal fun TimeRoutineEditUiValidUiState.reduceValidResultState(validResult: ResultState.Error): TimeRoutineEditUiValidUiState {
    val errorMessage = validResult.throwable.message?.let {
        UiText.Res(id = CommonR.string.message_failed_valid_check)
    } ?: UiText.Res(id = CommonR.string.message_failed_valid_check)
    val newState = this.copy(
        isValid = false,
        invalidTitleMessage = errorMessage
    )
    return newState
}

internal fun TimeRoutineEditUiValidUiState.reduceValidDomainResult(validResult: DomainResult<Boolean>): TimeRoutineEditUiValidUiState {
    return when (validResult) {
        is DomainResult.Failure -> {
            val error = validResult.error
            val newState = this.copy(
                isValid = false
            )
            when (error) {
                DomainError.Validation.Title -> {
                    newState.copy(
                        invalidTitleMessage = error.toUiText()
                    )
                }

                DomainError.Validation.NoSelectedDayOfWeek,
                DomainError.Conflict.DayOfWeek,
                    -> {
                    newState.copy(
                        invalidDayOfWeekMessage = error.toUiText()
                    )
                }

                else -> {
                    newState
                }
            }
        }

        is DomainResult.Success -> {
            if (validResult.value) {
                TimeRoutineEditUiValidUiState(isValid = true)
            } else {
                this.copy(isValid = false)
            }
        }
    }
}
