package software.seriouschoi.timeisgold.feature.timeroutine.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import software.seriouschoi.timeisgold.core.common.ui.ResultState
import software.seriouschoi.timeisgold.core.common.ui.withResultStateLifecycle
import software.seriouschoi.timeisgold.core.common.util.MetaEnvelope
import software.seriouschoi.timeisgold.core.domain.mapper.DomainErrorException
import software.seriouschoi.timeisgold.core.domain.mapper.asResultState
import software.seriouschoi.timeisgold.domain.data.DomainError
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.vo.TimeRoutineVO
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.WatchAllRoutineDayOfWeeksUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.WatchRoutineUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.WatchSelectableDayOfWeeksUseCase
import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Created by jhchoi on 2025. 10. 4.
 * jhchoi
 */
internal class TimeRoutineFeatureState(
    private val watchRoutineUseCase: WatchRoutineUseCase,
    private val watchSelectableDayOfWeeksUseCase: WatchSelectableDayOfWeeksUseCase
) {
    private val _data = MutableStateFlow(TimeRoutineFeatureStateData(defaultDayOfWeek))

    val data: StateFlow<TimeRoutineFeatureStateData> = _data

    @OptIn(ExperimentalCoroutinesApi::class)
    val routine: Flow<DomainResult<MetaEnvelope<TimeRoutineVO>?>> = data.map {
        it.dayOfWeek
    }.flatMapLatest {
        watchRoutineUseCase.invoke(it)
    }

    /**
     * 현재 선택 가능한 요일.
     * 다른 루틴이 선택하지 않은 요일들.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val selectableDayOfWeeks: Flow<DomainResult<List<DayOfWeek>>> = data.flatMapLatest {
        watchSelectableDayOfWeeksUseCase.invoke(it.dayOfWeek)
    }

    fun selectDayOfWeek(dayOfWeek: DayOfWeek) {
        _data.update {
            it.copy(dayOfWeek = dayOfWeek)
        }
    }

    companion object {
        private val defaultDayOfWeek
            get() = DayOfWeek.from(LocalDate.now())
    }
}