package software.seriouschoi.timeisgold.domain.usecase.timeslot

import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.asDomainResult
import software.seriouschoi.timeisgold.domain.data.entities.TimeSlotEntity
import software.seriouschoi.timeisgold.domain.port.TimeSlotRepositoryPort
import software.seriouschoi.timeisgold.domain.services.TimeSlotDomainService
import javax.inject.Inject

class SetTimeSlotUseCase @Inject constructor(
    private val timeslotRepositoryPort: TimeSlotRepositoryPort,
    private val timeSlotDomainService: TimeSlotDomainService
) {

    suspend operator fun invoke(
        timeRoutineUuid: String,
        timeSlotData: TimeSlotEntity
    ): DomainResult<String> {
        val validResult = timeSlotDomainService.isValid(
            routineUuid = timeRoutineUuid,
            timeSlotData = timeSlotData
        )
        if(validResult is DomainResult.Failure) return validResult

        val dataResult = timeslotRepositoryPort.setTimeSlot(
            timeSlotData,
            timeRoutineUuid
        )
        return dataResult.asDomainResult()
    }
}


