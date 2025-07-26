package software.seriouschoi.timeisgold.domain.usecase

import software.seriouschoi.timeisgold.domain.data.TimeSlotDetailData
import software.seriouschoi.timeisgold.domain.repositories.TimeSlotRepository

class SetTimeSlotUseCase(
    private val timeslotRepository: TimeSlotRepository
) {
    suspend operator fun invoke(timeSlotData: TimeSlotDetailData) {
        timeslotRepository.addTimeSlot(timeSlotData)
    }
}