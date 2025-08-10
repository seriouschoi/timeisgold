package software.seriouschoi.timeisgold.domain.usecase.timeslot

import software.seriouschoi.timeisgold.domain.data.timeslot.TimeSlotDetailData
import software.seriouschoi.timeisgold.domain.policy.TimeSlotPolicy
import software.seriouschoi.timeisgold.domain.repositories.TimeScheduleRepository
import software.seriouschoi.timeisgold.domain.repositories.TimeSlotRepository

class SetTimeSlotUseCase(
    private val timeslotRepository: TimeSlotRepository,
    private val timeScheduleRepository: TimeScheduleRepository,
    private val timeslotPolicy: TimeSlotPolicy
) {

    suspend operator fun invoke(timeScheduleUuid: String, timeSlotData: TimeSlotDetailData) {
        val timeScheduleDetail = timeScheduleRepository.getTimeScheduleDetailByUuid(timeScheduleUuid)
            ?: throw IllegalStateException("time schedule is null")

        timeslotPolicy.checkCanAdd(timeScheduleDetail.timeSlotList, timeSlotData.timeSlotData)

        timeslotRepository.setTimeSlot(timeSlotData)
    }
}