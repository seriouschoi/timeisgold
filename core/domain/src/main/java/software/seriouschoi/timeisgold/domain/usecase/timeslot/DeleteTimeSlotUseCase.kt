package software.seriouschoi.timeisgold.domain.usecase.timeslot

import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.asDomainResult
import software.seriouschoi.timeisgold.domain.port.NewSlotRepositoryPort
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 11. 2.
 * jhchoi
 */
class DeleteTimeSlotUseCase @Inject constructor(
    private val slotRepository: NewSlotRepositoryPort,
) {
    suspend fun invoke(slotId: String): DomainResult<Unit> {
        return slotRepository.deleteTimeSlot(slotId).asDomainResult()
    }
}