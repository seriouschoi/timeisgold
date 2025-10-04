package software.seriouschoi.timeisgold.domain.services

import kotlinx.coroutines.flow.first
import software.seriouschoi.timeisgold.core.common.util.LocalTimeUtil
import software.seriouschoi.timeisgold.core.common.util.asMinutes
import software.seriouschoi.timeisgold.domain.data.DomainError
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.entities.TimeSlotEntity
import software.seriouschoi.timeisgold.domain.policy.TimeSlotPolicy
import software.seriouschoi.timeisgold.domain.port.TimeSlotRepositoryPort
import javax.inject.Inject
import kotlin.math.abs

class TimeSlotDomainService @Inject constructor(
    val timeSlotRepository: TimeSlotRepositoryPort,
    private val timeSlotPolicy: TimeSlotPolicy
) {

    suspend fun isValid(
        timeSlotData: TimeSlotEntity,
        routineUuid: String,
    ): DomainResult<Unit> {
        val newSlotTitle = timeSlotData.title
        if (newSlotTitle.length !in timeSlotPolicy.titleLengthRange) {
            return DomainResult.Failure(DomainError.Validation.TitleLength)
        }

        val allTimeSlotList = timeSlotRepository.watchTimeSlotList(routineUuid).first()
            .filter {
                it.uuid != timeSlotData.uuid
            }.toMutableList().apply {
                this.add(timeSlotData)
            }

        return isValid(allTimeSlotList)
    }

    suspend fun isValid(
        timeSlotList: List<TimeSlotEntity>
    ): DomainResult<Unit> {
        val sorted = timeSlotList.sortedBy {
            it.endTime.asMinutes()
        }.sortedBy {
            it.startTime.asMinutes()
        }.distinct()

        var current: TimeSlotEntity? = null
        for (next in sorted) {

            val slotValid = next.isValid()
            if (slotValid is DomainResult.Failure) {
                return slotValid
            }

            if (current == null) {
                current = next
                continue
            }
            val overlap = LocalTimeUtil.overlab(
                current.startTime to current.endTime,
                next.startTime to next.endTime
            )

            if (overlap) return DomainResult.Failure(DomainError.Conflict.Data)
        }

        return DomainResult.Success(Unit)
    }
}

private fun TimeSlotEntity.isValid(): DomainResult<Unit> {
    if (abs(this.endTime.asMinutes() - this.startTime.asMinutes()) < 15) {
        return DomainResult.Failure(DomainError.Conflict.Time)
    }
    return DomainResult.Success(Unit)
}
