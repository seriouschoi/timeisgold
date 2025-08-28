package software.seriouschoi.timeisgold.domain.usecase.timeslot

import software.seriouschoi.timeisgold.domain.composition.TimeSlotComposition
import software.seriouschoi.timeisgold.domain.exception.TIGException
import software.seriouschoi.timeisgold.domain.policy.TimeSlotPolicy
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import software.seriouschoi.timeisgold.domain.port.TimeSlotRepositoryPort
import javax.inject.Inject

class AddTimeSlotUseCase @Inject constructor(
    private val timeslotRepositoryPort: TimeSlotRepositoryPort,
    private val timeRoutineRepositoryPort: TimeRoutineRepositoryPort,
    private val timeslotPolicy: TimeSlotPolicy
) {
    suspend operator fun invoke(timeRoutineUuid: String, timeSlotData: TimeSlotComposition) {
        // TODO: jhchoi 2025. 8. 28. usecase에 누락된 작업 진행. 
        val timeRoutineDetail = timeRoutineRepositoryPort.getTimeRoutineCompositionByUuid(timeRoutineUuid)
            ?: throw TIGException.TimeRoutineNotFound(timeRoutineUuid)

        timeslotPolicy.checkCanAdd(timeRoutineDetail.timeSlotList, timeSlotData.timeSlotData)

        timeslotRepositoryPort.addTimeSlot(timeSlotData, timeRoutineUuid)
    }
}