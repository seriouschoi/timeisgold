package software.seriouschoi.timeisgold.domain.usecase

import software.seriouschoi.timeisgold.domain.data.TimeSlotData
import software.seriouschoi.timeisgold.domain.repositories.TimeSlotRepository

class GetTimeSlotUseCase(
    private val timeslotRepository: TimeSlotRepository
) {
    operator fun invoke(uuid: String): TimeSlotData {
        return timeslotRepository.getTimeSlot(uuid)
    }
}