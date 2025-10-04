package software.seriouschoi.timeisgold.domain.usecase.timeslot

import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.asDomainResult
import software.seriouschoi.timeisgold.domain.data.entities.TimeSlotEntity
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import software.seriouschoi.timeisgold.domain.services.TimeSlotDomainService
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 9. 30.
 * jhchoi
 */
class SetTimeSlotListUseCase @Inject constructor(
    private val timeRoutineRepositoryPort: TimeRoutineRepositoryPort,
    private val timeSlotDomainService: TimeSlotDomainService
) {
    suspend fun invoke(timeRoutineUuid: String, timeSlotList: List<TimeSlotEntity>): DomainResult<Unit> {
        val validResult = timeSlotDomainService.isValid(timeSlotList)
        if (validResult is DomainResult.Failure) return validResult

        return timeRoutineRepositoryPort.setTimeSlotList(
            routineUuid = timeRoutineUuid,
            incomingSlots = timeSlotList.distinct()
        ).asDomainResult()
    }
}