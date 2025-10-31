package software.seriouschoi.timeisgold.domain.usecase.timeslot.valid

import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.vo.TimeSlotVO
import software.seriouschoi.timeisgold.domain.services.TimeSlotDomainService
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 10. 8.
 * jhchoi
 */
class GetTimeSlotPolicyValidUseCase @Inject constructor(
    private val service: TimeSlotDomainService,
) {

    fun invoke(slotEntity: TimeSlotVO): DomainResult<Unit> {
        return service.isPolicyValid(slotEntity)
    }
}