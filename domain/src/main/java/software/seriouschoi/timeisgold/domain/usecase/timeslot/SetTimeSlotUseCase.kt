package software.seriouschoi.timeisgold.domain.usecase.timeslot

import software.seriouschoi.timeisgold.domain.data.timeslot.TimeSlotDetailData
import software.seriouschoi.timeisgold.domain.policy.TimeSlotPolicy
import software.seriouschoi.timeisgold.domain.repositories.TimeRoutineRepository
import software.seriouschoi.timeisgold.domain.repositories.TimeSlotRepository
import javax.inject.Inject

class SetTimeSlotUseCase @Inject constructor(
    private val timeslotRepository: TimeSlotRepository,
    private val timeRoutineRepository: TimeRoutineRepository,
    private val timeslotPolicy: TimeSlotPolicy
) {

    suspend operator fun invoke(timeRoutineUuid: String, timeSlotData: TimeSlotDetailData) {
        val timeRoutineDetail = timeRoutineRepository.getTimeRoutineDetailByUuid(timeRoutineUuid)
            ?: throw IllegalStateException("time routine is null")

        timeslotPolicy.checkCanAdd(timeRoutineDetail.timeSlotList, timeSlotData.timeSlotData)

        timeslotRepository.setTimeSlot(timeSlotData)
    }
}