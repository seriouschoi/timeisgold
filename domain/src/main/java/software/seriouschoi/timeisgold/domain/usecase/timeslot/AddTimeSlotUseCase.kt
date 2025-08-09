package software.seriouschoi.timeisgold.domain.usecase.timeslot

import software.seriouschoi.timeisgold.domain.data.time_schedule.TimeScheduleData
import software.seriouschoi.timeisgold.domain.data.timeslot.TimeSlotDetailData
import software.seriouschoi.timeisgold.domain.repositories.TimeSlotRepository

class AddTimeSlotUseCase(
    private val timeslotRepository: TimeSlotRepository
) {
    suspend operator fun invoke(timeScheduleData: TimeScheduleData, timeSlotData: TimeSlotDetailData) {
        timeslotRepository.addTimeSlot(timeSlotData, timeScheduleData.uuid)
    }
}