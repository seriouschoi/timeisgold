package software.seriouschoi.timeisgold.domain.usecase

import software.seriouschoi.timeisgold.domain.data.TimeSlotDetailData
import software.seriouschoi.timeisgold.domain.repositories.TimeSlotRepository

class GetTimeSlotUseCase(
    private val timeslotRepository: TimeSlotRepository
) {
    suspend operator fun invoke(uuid: String): TimeSlotDetailData? {
        return timeslotRepository.getTimeSlot(uuid)
    }
}