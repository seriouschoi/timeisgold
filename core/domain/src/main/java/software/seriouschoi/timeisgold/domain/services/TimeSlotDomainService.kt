package software.seriouschoi.timeisgold.domain.services

import kotlinx.coroutines.flow.first
import software.seriouschoi.timeisgold.core.common.util.LocalTimeUtil
import software.seriouschoi.timeisgold.core.common.util.asMinutes
import software.seriouschoi.timeisgold.domain.data.DomainError
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.entities.TimeSlotEntity
import software.seriouschoi.timeisgold.domain.data.vo.TimeSlotVO
import software.seriouschoi.timeisgold.domain.policy.TimeSlotPolicy
import software.seriouschoi.timeisgold.domain.port.TimeSlotRepositoryPort
import javax.inject.Inject
import kotlin.math.abs

class TimeSlotDomainService @Inject constructor(
    private val timeSlotRepository: TimeSlotRepositoryPort,
    private val timeSlotPolicy: TimeSlotPolicy
) {

    suspend fun isValid(
        timeSlotData: TimeSlotEntity,
        routineUuid: String,
    ): DomainResult<Unit> {
        val policyResult = isPolicyValid(timeSlotData)
        if (policyResult is DomainResult.Failure) {
            return policyResult
        }

        val allTimeSlotList = timeSlotRepository.watchTimeSlotList(routineUuid).first()
            .filter {
                it.uuid != timeSlotData.uuid
            }.toMutableList().apply {
                this.add(timeSlotData)
            }

        return isConflict(allTimeSlotList)
    }

    fun isPolicyValid(entity: TimeSlotEntity): DomainResult<Unit> {
        if (entity.title.length !in timeSlotPolicy.titleLengthRange) {
            return DomainResult.Failure(DomainError.Validation.TitleLength)
        }
        if (abs(entity.endTime.asMinutes() - entity.startTime.asMinutes()) < 15) {
            return DomainResult.Failure(DomainError.Conflict.Time)
        }
        return DomainResult.Success(Unit)
    }

    fun isPolicyValid(entity: TimeSlotVO): DomainResult<Unit> {
        if (abs(entity.endTime.asMinutes() - entity.startTime.asMinutes()) < 15) {
            return DomainResult.Failure(DomainError.Conflict.Time)
        }
        return DomainResult.Success(Unit)
    }

    fun isConflict(
        timeSlotList: List<TimeSlotEntity>
    ): DomainResult<Unit> {
        val sorted = timeSlotList.sortedBy {
            it.endTime.asMinutes()
        }.sortedBy {
            it.startTime.asMinutes()
        }.distinct()

        var current: TimeSlotEntity? = null
        for (next in sorted) {

            val slotValid = isPolicyValid(next)
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
