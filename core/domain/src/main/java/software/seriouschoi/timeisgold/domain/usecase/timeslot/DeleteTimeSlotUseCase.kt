package software.seriouschoi.timeisgold.domain.usecase.timeslot

import software.seriouschoi.timeisgold.domain.port.TimeSlotRepositoryPort
import javax.inject.Inject

class DeleteTimeSlotUseCase @Inject constructor(
    private val timeSlotRepositoryPort: TimeSlotRepositoryPort
) {
    suspend operator fun invoke(timeSlotUuid: String) {
        timeSlotRepositoryPort.deleteTimeSlot(timeSlotUuid)
    }
}