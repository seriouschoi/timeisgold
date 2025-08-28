package software.seriouschoi.timeisgold.domain.usecase.timeslot

import kotlinx.coroutines.flow.first
import software.seriouschoi.timeisgold.domain.composition.TimeSlotComposition
import software.seriouschoi.timeisgold.domain.policy.TimeSlotPolicy
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import software.seriouschoi.timeisgold.domain.port.TimeSlotRepositoryPort
import javax.inject.Inject

class AddTimeSlotUseCase @Inject constructor(
    private val timeSlotRepo: TimeSlotRepositoryPort,
    private val timeslotPolicy: TimeSlotPolicy
) {
    suspend operator fun invoke(timeRoutineUuid: String, timeSlotData: TimeSlotComposition) {
        val timeSlots = timeSlotRepo.getTimeSlotList(timeRoutineUuid).first()
        timeslotPolicy.checkCanAdd(timeSlots, timeSlotData.timeSlotData)

        timeSlotRepo.addTimeSlot(timeSlotData, timeRoutineUuid)
    }
}