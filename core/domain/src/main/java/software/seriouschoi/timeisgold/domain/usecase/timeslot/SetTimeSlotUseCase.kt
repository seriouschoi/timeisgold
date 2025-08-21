package software.seriouschoi.timeisgold.domain.usecase.timeslot

import software.seriouschoi.timeisgold.domain.data.timeslot.TimeSlotDetailData
import software.seriouschoi.timeisgold.domain.policy.TimeSlotPolicy
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import software.seriouschoi.timeisgold.domain.port.TimeSlotRepositoryPort
import javax.inject.Inject

class SetTimeSlotUseCase @Inject constructor(
    private val timeslotRepositoryPort: TimeSlotRepositoryPort,
    private val timeRoutineRepositoryPort: TimeRoutineRepositoryPort,
    private val timeslotPolicy: TimeSlotPolicy
) {

    suspend operator fun invoke(timeRoutineUuid: String, timeSlotData: TimeSlotDetailData) {
        val timeRoutineDetail = timeRoutineRepositoryPort.getTimeRoutineDetailByUuid(timeRoutineUuid)
            ?: throw IllegalStateException("time routine is null")

        timeslotPolicy.checkCanAdd(timeRoutineDetail.timeSlotList, timeSlotData.timeSlotData)

        timeslotRepositoryPort.setTimeSlot(timeSlotData)
    }
}