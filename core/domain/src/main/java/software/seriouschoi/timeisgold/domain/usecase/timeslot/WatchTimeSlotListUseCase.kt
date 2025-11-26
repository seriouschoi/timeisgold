package software.seriouschoi.timeisgold.domain.usecase.timeslot

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import software.seriouschoi.timeisgold.core.common.util.MetaEnvelope
import software.seriouschoi.timeisgold.domain.data.DataError
import software.seriouschoi.timeisgold.domain.data.DataResult
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
        /*
        요일의 루틴을 찾아서,
        있으면, 루틴의 타임 슬롯목록.
        없으면, 비어있는 목록 리턴.
         */
        val routineFlow = timeRoutineRepository.watchRoutine(dayOfWeek)
        return routineFlow.flatMapLatest { result ->
            val failure = (result as? DataResult.Failure)?.takeIf { it.error !is DataError.NotFound }
            if (failure != null) {
                return@flatMapLatest flowOf(failure)
            } else {
                when(result) {
                    is DataResult.Failure ->{
                        flowOf(DataResult.Success(emptyList()))
                    }
                    is DataResult.Success -> {
                        val routineId = result.value.metaInfo.uuid
                        timeSlotRepository.watchTimeSlotList(routineId = routineId)
                    }
                }
            }
        }.map {
            it.asDomainResult()
        }
    }
}