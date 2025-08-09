package software.seriouschoi.timeisgold.domain.usecase.timeslot

import software.seriouschoi.timeisgold.domain.data.timeslot.TimeSlotDetailData
import software.seriouschoi.timeisgold.domain.repositories.TimeSlotRepository

class SetTimeSlotUseCase(
    private val timeslotRepository: TimeSlotRepository
) {

    suspend fun setTimeSlot(timeSlotData: TimeSlotDetailData) {
        timeslotRepository.setTimeSlot(timeSlotData)
    }
}