package software.seriouschoi.timeisgold.domain.services

import software.seriouschoi.timeisgold.core.common.util.LocalTimeUtil
import software.seriouschoi.timeisgold.core.common.util.asMinutes
import software.seriouschoi.timeisgold.domain.data.DomainError
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.vo.TimeSlotVO
import javax.inject.Inject
import kotlin.math.abs

class TimeSlotDomainService @Inject constructor(){
    fun isPolicyValid(entity: TimeSlotVO): DomainResult<Unit> {
        if (abs(entity.endTime.asMinutes() - entity.startTime.asMinutes()) < 15) {
            return DomainResult.Failure(DomainError.Conflict.Time)
        }
        return DomainResult.Success(Unit)
    }

    fun isConflict(
        timeSlotList: List<TimeSlotVO>
    ): DomainResult<Unit> {
        val sorted = timeSlotList.sortedBy {
            it.endTime.asMinutes()
        }.sortedBy {
            it.startTime.asMinutes()
        }.distinct()

        var current: TimeSlotVO? = null
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
