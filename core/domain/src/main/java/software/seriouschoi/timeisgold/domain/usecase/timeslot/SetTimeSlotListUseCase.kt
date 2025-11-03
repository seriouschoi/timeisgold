package software.seriouschoi.timeisgold.domain.usecase.timeslot

import software.seriouschoi.timeisgold.core.common.util.MetaInfo
import software.seriouschoi.timeisgold.domain.data.DomainError
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.asDomainResult
import software.seriouschoi.timeisgold.domain.data.vo.TimeSlotVO
import software.seriouschoi.timeisgold.domain.port.TimeSlotRepositoryPort
import software.seriouschoi.timeisgold.domain.services.TimeRoutineDomainService
import software.seriouschoi.timeisgold.domain.services.TimeSlotDomainService
import java.time.DayOfWeek
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 9. 30.
 * jhchoi
 */
class SetTimeSlotListUseCase @Inject constructor(
    private val timeSlotDomainService: TimeSlotDomainService,
    private val slotRepository: TimeSlotRepositoryPort,
    private val routineService: TimeRoutineDomainService
) {
    suspend fun invoke(
        dayOfWeek: DayOfWeek,
        timeSlotMap: Map<String, TimeSlotVO>
    ): DomainResult<List<MetaInfo>> {
        val validResult = timeSlotDomainService.isConflict(timeSlotMap.map { it.value })
        if (validResult is DomainResult.Failure) return validResult

        val routineId =
            routineService.getOrCreateRoutineId(dayOfWeek).let {
                it as? DomainResult.Success
            }?.value ?: return DomainResult.Failure(
                DomainError.NotFound.TimeRoutine
            )

        return slotRepository.setTimeSlots(
            timeSlots = timeSlotMap,
            routineId = routineId
        ).asDomainResult()
    }
}