package software.seriouschoi.timeisgold.domain.usecase.timeslot

import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.asDomainResult
import software.seriouschoi.timeisgold.domain.port.TimeSlotRepositoryPort
import javax.inject.Inject

class DeleteTimeSlotUseCase @Inject constructor(
    private val timeSlotRepositoryPort: TimeSlotRepositoryPort
) {
    suspend operator fun invoke(timeSlotUuid: String): DomainResult<Unit> {
        return timeSlotRepositoryPort.deleteTimeSlot(timeSlotUuid).asDomainResult()
    }
}