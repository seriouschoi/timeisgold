package software.seriouschoi.timeisgold.domain.usecase.timeslot

import kotlinx.coroutines.flow.first
import software.seriouschoi.timeisgold.domain.data.composition.TimeSlotComposition
import software.seriouschoi.timeisgold.domain.policy.TimeSlotPolicy
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import software.seriouschoi.timeisgold.domain.port.TimeSlotRepositoryPort
import javax.inject.Inject

class SetTimeSlotUseCase @Inject constructor(
    private val timeslotRepositoryPort: TimeSlotRepositoryPort,
    private val timeslotPolicy: TimeSlotPolicy,
) {

    suspend operator fun invoke(timeRoutineUuid: String, timeSlotData: TimeSlotComposition) {
        val timeSlots = timeslotRepositoryPort.getTimeSlotList(timeRoutineUuid).first()
        timeslotPolicy.checkCanAdd(timeSlots, timeSlotData.timeSlotData)

        timeslotRepositoryPort.setTimeSlot(timeSlotData)
    }
}