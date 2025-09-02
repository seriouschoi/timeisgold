package software.seriouschoi.timeisgold.domain.usecase.timeslot

import kotlinx.coroutines.flow.Flow
import software.seriouschoi.timeisgold.domain.data.composition.TimeSlotComposition
import software.seriouschoi.timeisgold.domain.port.TimeSlotRepositoryPort
import javax.inject.Inject

class GetTimeSlotDetailUseCase @Inject constructor(
    private val timeslotRepositoryPort: TimeSlotRepositoryPort
) {
    suspend operator fun invoke(timeslotUuid: String): Flow<TimeSlotComposition?> {
        return timeslotRepositoryPort.getTimeSlotDetail(timeslotUuid = timeslotUuid)
    }
}