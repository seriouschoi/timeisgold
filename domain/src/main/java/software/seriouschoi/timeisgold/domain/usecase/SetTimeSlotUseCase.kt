package software.seriouschoi.timeisgold.domain.usecase

import software.seriouschoi.timeisgold.domain.data.TimeSlotData
import software.seriouschoi.timeisgold.domain.repositories.TimeSlotRepository

class SetTimeSlotUseCase(
    private val timeslotRepository: TimeSlotRepository
) {
    operator fun invoke(timeSlotData: TimeSlotData) {
        timeslotRepository.setTimeSlot(timeSlotData)
    }
}