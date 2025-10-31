package software.seriouschoi.timeisgold.feature.timeroutine.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import software.seriouschoi.timeisgold.core.common.util.MetaEnvelope
import software.seriouschoi.timeisgold.core.domain.mapper.onlySuccess
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.vo.TimeRoutineVO
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.WatchAllRoutineDayOfWeeksUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.WatchRoutineUseCase
import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Created by jhchoi on 2025. 10. 4.
 * jhchoi
 */
internal class TimeRoutineFeatureState(
    private val allDayOfWeeksUseCase: WatchAllRoutineDayOfWeeksUseCase,
    private val watchRoutineUseCase: WatchRoutineUseCase
) {
    private val _data = MutableStateFlow(TimeRoutineFeatureStateData(defaultDayOfWeek))

    val data: StateFlow<TimeRoutineFeatureStateData> = _data

    @OptIn(ExperimentalCoroutinesApi::class)
    val routine: Flow<DomainResult<MetaEnvelope<TimeRoutineVO>>> = data.map {
        it.dayOfWeek
    }.flatMapLatest {
        watchRoutineUseCase.invoke(it)
    }

    val selectableDayOfWeeks = combine(
        allDayOfWeeksUseCase.invoke(),
        routine.map { it.onlySuccess() }
    ) { allDayOfWeeks, routine ->

        val allRoutinesDayOfWeeks = allDayOfWeeks.onlySuccess() ?: emptyList()
        val currentRoutineDayOfWeeks = routine?.payload?.dayOfWeeks ?: emptyList()

        DayOfWeek.entries.filter { day ->
            val usedByOtherRoutine = allRoutinesDayOfWeeks.contains(day)
            val usedByCurrentRoutine = currentRoutineDayOfWeeks.contains(day)

            // 현재 루틴에서 사용 중이거나, 다른 루틴에서 사용 중이 아닌 경우만 활성
            !usedByOtherRoutine || usedByCurrentRoutine
        }
    }


    fun reduce(intent: TimeRoutineFeatureStateIntent) {
        when (intent) {
            is TimeRoutineFeatureStateIntent.SelectDayOfWeek -> {
                _data.update {
                    it.copy(dayOfWeek = intent.dayOfWeek)
                }
            }
        }
    }


    companion object {
        private val defaultDayOfWeek
            get() = DayOfWeek.from(LocalDate.now())
    }
}

internal sealed interface TimeRoutineFeatureStateIntent {
    data class SelectDayOfWeek(val dayOfWeek: DayOfWeek) : TimeRoutineFeatureStateIntent

}