package software.seriouschoi.timeisgold.domain.usecase.timeroutine

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import software.seriouschoi.timeisgold.core.common.util.MetaEnvelope
import software.seriouschoi.timeisgold.domain.data.DataError
import software.seriouschoi.timeisgold.domain.data.DataResult
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.asDomainResult
import software.seriouschoi.timeisgold.domain.data.vo.TimeRoutineVO
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import java.time.DayOfWeek
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 11. 17.
 * jhchoi
 */
class WatchSelectableDayOfWeeksUseCase @Inject constructor(
    private val timeRoutineRepository: TimeRoutineRepositoryPort
) {
    fun invoke(dayOfWeek: DayOfWeek): Flow<DomainResult<List<DayOfWeek>>> {
        val allDayOfWeeksSource = timeRoutineRepository.watchAllDayOfWeeks()
        val currentRoutineSource = timeRoutineRepository.watchRoutine(dayOfWeek)

        return combine(
            allDayOfWeeksSource,
            currentRoutineSource
        ) { allDayOfWeeks, currentRoutine ->
            resolveFailureOrNull(
                allDayOfWeeks, currentRoutine
            ) ?: resolveSelectableDays(
                allDayOfWeeks, currentRoutine
            )
        }
    }

    private fun resolveSelectableDays(
        allDayOfWeeks: DataResult<Set<DayOfWeek>>,
        currentRoutine: DataResult<MetaEnvelope<TimeRoutineVO>?>
    ): DomainResult<List<DayOfWeek>> {
        val usedDayOfWeeks = when (allDayOfWeeks) {
            is DataResult.Success -> allDayOfWeeks.value
            is DataResult.Failure -> emptySet()
        }
        val currentDayOfWeeks = when (currentRoutine) {
            is DataResult.Success -> currentRoutine.value?.payload?.dayOfWeeks
                ?: emptySet()

            is DataResult.Failure -> emptySet()
        }

        val selectableDayOfWeeks = DayOfWeek.entries.filter { day ->
            val usedByOtherRoutine = day in usedDayOfWeeks
            val usedByCurrentRoutine = day in currentDayOfWeeks

            // 현재 루틴에서 사용 중이거나, 다른 루틴에서 사용 중이 아닌 경우만 활성
            !usedByOtherRoutine || usedByCurrentRoutine
        }

        return DomainResult.Success(selectableDayOfWeeks)
    }

    private fun resolveFailureOrNull(
        allDayOfWeeks: DataResult<Set<DayOfWeek>>,
        currentRoutine: DataResult<MetaEnvelope<TimeRoutineVO>?>
    ): DomainResult<List<DayOfWeek>>? {
        //not found 이외 실패는 domainError.
        val allFailure =
            (allDayOfWeeks as? DataResult.Failure)?.takeIf { it.error !is DataError.NotFound }
        val currentFailure =
            (currentRoutine as? DataResult.Failure)?.takeIf { it.error !is DataError.NotFound }

        if (allFailure != null) {
            return allFailure.asDomainResult()
        }
        if (currentFailure != null) {
            return currentFailure.asDomainResult()
        }

        return null
    }
}