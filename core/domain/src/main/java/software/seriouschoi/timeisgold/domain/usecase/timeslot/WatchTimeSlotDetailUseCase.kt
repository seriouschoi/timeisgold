package software.seriouschoi.timeisgold.domain.usecase.timeslot

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import software.seriouschoi.timeisgold.domain.data.DomainError
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.entities.TimeSlotEntity
import software.seriouschoi.timeisgold.domain.port.TimeSlotRepositoryPort
import javax.inject.Inject

class WatchTimeSlotDetailUseCase @Inject constructor(
    private val timeslotRepositoryPort: TimeSlotRepositoryPort
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(timeslotUuid: String): Flow<DomainResult<TimeSlotEntity>> {
        return timeslotRepositoryPort.watchTimeSlotDetail(timeslotUuid = timeslotUuid)
            .flatMapLatest {
                if (it == null) {
                    flowOf(DomainResult.Failure(DomainError.NotFound.TimeSlot))
                } else {
                    flowOf(DomainResult.Success(it))
                }
            }
    }
}