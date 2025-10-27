package software.seriouschoi.timeisgold.domain.usecase.timeslot

import software.seriouschoi.timeisgold.core.common.util.MetaEnvelope
import software.seriouschoi.timeisgold.core.common.util.MetaInfo
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.asDomainResult
import software.seriouschoi.timeisgold.domain.data.vo.TimeSlotVO
import software.seriouschoi.timeisgold.domain.port.TimeSlotRepositoryPort
import software.seriouschoi.timeisgold.domain.services.TimeSlotDomainService
import java.time.DayOfWeek
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 10. 27.
 * jhchoi
 */
class SetDayOfWeekTimeSlotUseCase @Inject constructor(
    private val timeSlotRepositoryPort: TimeSlotRepositoryPort,
    private val timeSlotDomainService: TimeSlotDomainService
) {
    suspend fun invoke(
        dayOfWeek: DayOfWeek,
        timeSlotData: MetaEnvelope<TimeSlotVO>
    ): DomainResult<MetaInfo> {
        val policyResult = timeSlotDomainService.isPolicyValid(
            timeSlotData.payload
        )
        if (policyResult is DomainResult.Failure) return policyResult

        return timeSlotRepositoryPort.setTimeSlot(
            timeSlotEnvelope = timeSlotData,
            dayOfWeek = dayOfWeek
        ).asDomainResult()
    }
}