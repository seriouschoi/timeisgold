package software.seriouschoi.timeisgold.domain.usecase.timeslot

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import software.seriouschoi.timeisgold.core.common.util.MetaEnvelope
import software.seriouschoi.timeisgold.domain.data.DomainError
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.asDomainResult
import software.seriouschoi.timeisgold.domain.data.vo.TimeRoutineVO
import software.seriouschoi.timeisgold.domain.data.vo.TimeSlotVO
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import software.seriouschoi.timeisgold.domain.port.TimeSlotRepositoryPort
import java.time.DayOfWeek
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 10. 28.
 * jhchoi
 */
class WatchTimeSlotListUseCase @Inject constructor(
    private val timeSlotRepository: TimeSlotRepositoryPort,
    private val timeRoutineRepository: TimeRoutineRepositoryPort
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(dayOfWeek: DayOfWeek): Flow<DomainResult<List<MetaEnvelope<TimeSlotVO>>>> {
        return timeRoutineRepository.watchRoutine(dayOfWeek).map {
            it.asDomainResult()
        }.flatMapLatest { it: DomainResult<MetaEnvelope<TimeRoutineVO>> ->
            when (it) {
                is DomainResult.Failure -> {
                    flowOf(DomainResult.Failure(DomainError.NotFound.TimeRoutine))
                }

                is DomainResult.Success -> {
                    val routineId = it.value.metaInfo.uuid
                    timeSlotRepository.watchTimeSlotList(routineId = routineId).map {
                        it.asDomainResult()
                    }
                }
            }
        }
    }
}