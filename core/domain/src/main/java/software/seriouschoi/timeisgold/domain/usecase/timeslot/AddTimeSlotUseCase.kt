package software.seriouschoi.timeisgold.domain.usecase.timeslot

import software.seriouschoi.timeisgold.domain.data.composition.TimeSlotComposition
import software.seriouschoi.timeisgold.domain.port.TimeSlotRepositoryPort
import software.seriouschoi.timeisgold.domain.services.TimeSlotDomainService
import javax.inject.Inject

class AddTimeSlotUseCase @Inject constructor(
    private val timeSlotRepo: TimeSlotRepositoryPort,
    private val timeSlotDomainService: TimeSlotDomainService
) {
    suspend operator fun invoke(timeRoutineUuid: String, timeSlotData: TimeSlotComposition) {
        timeSlotDomainService.checkCanAdd(timeRoutineUuid, timeSlotData.timeSlotData)
        timeSlotRepo.addTimeSlot(timeSlotData, timeRoutineUuid)
    }
}