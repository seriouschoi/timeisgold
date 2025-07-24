package software.seriouschoi.timeisgold.domain.usecase

import software.seriouschoi.timeisgold.domain.repositories.TimeSlotRepository

class DeleteTimeSlotUseCase(
    private val timeSlotRepository: TimeSlotRepository
) {
    operator fun invoke(uuid: String) {
        timeSlotRepository.deleteTimeSlot(uuid)
    }
}