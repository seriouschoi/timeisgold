package software.seriouschoi.timeisgold.domain.usecase.timeslot

import software.seriouschoi.timeisgold.core.common.util.MetaInfo
import software.seriouschoi.timeisgold.domain.data.DomainError
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.asDomainResult
import software.seriouschoi.timeisgold.domain.data.vo.TimeSlotVO
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import software.seriouschoi.timeisgold.domain.port.TimeSlotRepositoryPort
import software.seriouschoi.timeisgold.domain.services.TimeRoutineDomainService
import software.seriouschoi.timeisgold.domain.services.TimeSlotDomainService
import java.time.DayOfWeek
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 10. 27.
 * jhchoi
 */
class SetTimeSlotUseCase @Inject constructor(
    private val slotRepository: TimeSlotRepositoryPort,
    private val routineRepository: TimeRoutineRepositoryPort,
    private val timeSlotDomainService: TimeSlotDomainService,
    private val timeRoutineService: TimeRoutineDomainService
) {
    suspend fun execute(
        dayOfWeek: DayOfWeek,
        timeSlot: TimeSlotVO,
        slotId: String?,
    ): DomainResult<MetaInfo> {
        val policyResult = timeSlotDomainService.isPolicyValid(
            timeSlot
        )
        if (policyResult is DomainResult.Failure) return policyResult

        val routineUuid = timeRoutineService.getOrCreateRoutineId(dayOfWeek).let {
            it as? DomainResult.Success
        }?.value ?: return DomainResult.Failure(DomainError.Technical.Unknown)

        return slotRepository.setTimeSlot(
            timeSlot = timeSlot,
            slotId = slotId,
            routineId = routineUuid
        ).asDomainResult()
    }
}