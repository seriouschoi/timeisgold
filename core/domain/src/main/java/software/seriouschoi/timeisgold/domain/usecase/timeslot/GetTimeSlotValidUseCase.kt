package software.seriouschoi.timeisgold.domain.usecase.timeslot

import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.entities.TimeSlotEntity
import software.seriouschoi.timeisgold.domain.services.TimeSlotDomainService
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 9. 19.
 * jhchoi
 */
class GetTimeSlotValidUseCase @Inject constructor(
    private val service: TimeSlotDomainService,
) {
    suspend fun invoke(
        slotEntity: TimeSlotEntity,
        routineUuid: String,
    ): DomainResult<Unit> = service.isValid(routineUuid, slotEntity)
}