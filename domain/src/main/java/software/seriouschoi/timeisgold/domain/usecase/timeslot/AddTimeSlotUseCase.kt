package software.seriouschoi.timeisgold.domain.usecase.timeslot

import software.seriouschoi.timeisgold.domain.data.timeslot.TimeSlotDetailData
import software.seriouschoi.timeisgold.domain.exception.TIGException
import software.seriouschoi.timeisgold.domain.policy.TimeSlotPolicy
import software.seriouschoi.timeisgold.domain.repositories.TimeRoutineRepository
import software.seriouschoi.timeisgold.domain.repositories.TimeSlotRepository

class AddTimeSlotUseCase(
    private val timeslotRepository: TimeSlotRepository,
    private val timeRoutineRepository: TimeRoutineRepository,
    private val timeslotPolicy: TimeSlotPolicy
) {
    suspend operator fun invoke(timeRoutineUuid: String, timeSlotData: TimeSlotDetailData) {
        val timeRoutineDetail = timeRoutineRepository.getTimeRoutineDetailByUuid(timeRoutineUuid)
            ?: throw TIGException.TimeRoutineNotFound(timeRoutineUuid)

        timeslotPolicy.checkCanAdd(timeRoutineDetail.timeSlotList, timeSlotData.timeSlotData)

        timeslotRepository.addTimeSlot(timeSlotData, timeRoutineUuid)
    }
}