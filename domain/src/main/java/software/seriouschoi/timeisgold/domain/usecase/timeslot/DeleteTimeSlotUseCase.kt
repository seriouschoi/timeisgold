package software.seriouschoi.timeisgold.domain.usecase.timeslot

import software.seriouschoi.timeisgold.domain.repositories.TimeSlotRepository

class DeleteTimeSlotUseCase(
    private val timeSlotRepository: TimeSlotRepository
) {
    suspend operator fun invoke(timeSlotUuid: String) {
        timeSlotRepository.deleteTimeSlot(timeSlotUuid)
    }
}