package software.seriouschoi.timeisgold.domain.usecase.timeslot

import software.seriouschoi.timeisgold.domain.composition.TimeSlotComposition
import software.seriouschoi.timeisgold.domain.port.TimeSlotRepositoryPort
import javax.inject.Inject

class GetTimeSlotDetailUseCase @Inject constructor(
    private val timeslotRepositoryPort: TimeSlotRepositoryPort
) {
    suspend operator fun invoke(timeslotUuid: String): TimeSlotComposition? {
        return timeslotRepositoryPort.getTimeSlotDetail(timeslotUuid = timeslotUuid)
    }
}