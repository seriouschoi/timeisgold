package software.seriouschoi.timeisgold.domain.usecase.timeslot

import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.entities.TimeSlotEntity
import software.seriouschoi.timeisgold.domain.port.TimeSlotRepositoryPort
import software.seriouschoi.timeisgold.domain.services.TimeSlotDomainService
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 9. 30.
 * jhchoi
 */
class SetTimeSlotListUseCase @Inject constructor(
    private val timeslotRepositoryPort: TimeSlotRepositoryPort,
    private val timeSlotDomainService: TimeSlotDomainService
) {
    fun invoke(timeRoutineUuid: String, timeSlotData: List<TimeSlotEntity>): DomainResult<Unit> {
        // TODO: jhchoi 2025. 9. 30.
        return DomainResult.Success(Unit)
    }
}