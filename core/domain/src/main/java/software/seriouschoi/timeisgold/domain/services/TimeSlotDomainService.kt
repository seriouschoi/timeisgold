package software.seriouschoi.timeisgold.domain.services

import kotlinx.coroutines.flow.first
import software.seriouschoi.timeisgold.core.common.util.LocalTimeUtil
import software.seriouschoi.timeisgold.core.common.util.asMinutes
import software.seriouschoi.timeisgold.domain.data.DomainError
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.entities.TimeSlotEntity
import software.seriouschoi.timeisgold.domain.port.TimeSlotRepositoryPort
import javax.inject.Inject
import kotlin.math.abs

class TimeSlotDomainService @Inject constructor(
    val timeSlotRepository: TimeSlotRepositoryPort,
) {

    suspend fun isValid(
        timeSlotData: TimeSlotEntity,
        routineUuid: String,
    ): DomainResult<Unit> {
        val newSlotTitle = timeSlotData.title
        if (newSlotTitle.length !in 1..15) {
            return DomainResult.Failure(DomainError.Validation.TitleLength)
        }

        val allTimeSlotList = timeSlotRepository.watchTimeSlotList(routineUuid).first()

        if (abs(timeSlotData.endTime.asMinutes() - timeSlotData.startTime.asMinutes()) <= 15) {
            return DomainResult.Failure(DomainError.Conflict.Time)
        }

        //timeslot의 시간이 겹치는지 확인하는 로직.
        val overlapTime = allTimeSlotList.filter {
            it.uuid != timeSlotData.uuid
        }.any { existTimeSlot ->
            LocalTimeUtil.overlab(
                existTimeSlot.startTime to existTimeSlot.endTime,
                timeSlotData.startTime to timeSlotData.endTime
            )
        }

        if (overlapTime) {
            return DomainResult.Failure(DomainError.Conflict.Data)
        }
        return DomainResult.Success(Unit)
    }

    suspend fun isValid(
        timeSlotList: List<TimeSlotEntity>
    ): DomainResult<Unit> {
        val sorted = timeSlotList.sortedBy {
            it.endTime.asMinutes()
        }.sortedBy {
            it.startTime.asMinutes()
        }

        var current: TimeSlotEntity? = null
        for (next in sorted) {
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