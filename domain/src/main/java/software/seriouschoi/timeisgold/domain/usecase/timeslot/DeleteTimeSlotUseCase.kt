package software.seriouschoi.timeisgold.domain.usecase.timeslot

import software.seriouschoi.timeisgold.domain.repositories.TimeSlotRepository
import javax.inject.Inject

class DeleteTimeSlotUseCase @Inject constructor(
    private val timeSlotRepository: TimeSlotRepository
) {
    suspend operator fun invoke(timeSlotUuid: String) {
        timeSlotRepository.deleteTimeSlot(timeSlotUuid)
    }
}