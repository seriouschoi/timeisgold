package software.seriouschoi.timeisgold.domain.usecase

import software.seriouschoi.timeisgold.domain.data.TimeSlotData
import software.seriouschoi.timeisgold.domain.repositories.TimeSlotRepository

class GetTimeSlotListUseCase(
    private val timeslotRepository: TimeSlotRepository
) {
    operator fun invoke(): List<TimeSlotData> {
        return timeslotRepository.getTimeSlotList()
    }
}