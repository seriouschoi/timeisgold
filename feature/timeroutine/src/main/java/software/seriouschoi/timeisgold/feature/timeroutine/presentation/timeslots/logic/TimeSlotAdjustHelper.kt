package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.logic

import software.seriouschoi.timeisgold.core.common.util.LocalTimeUtil
import software.seriouschoi.timeisgold.core.common.util.asMinutes
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.vo.TimeSlotVO
import software.seriouschoi.timeisgold.domain.usecase.timeslot.valid.GetTimeSlotPolicyValidUseCase
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.item.TimeSlotItemUiState
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.item.midMinute
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.item.splitOverMidnight
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.item.timeLog
import timber.log.Timber
import javax.inject.Inject

internal enum class TimeSlotChangeTimeType {
    START_TIME, END_TIME, START_AND_END
}
/**
 * Created by jhchoi on 2025. 10. 20.
 * jhchoi
 */
internal class TimeSlotAdjustHelper @Inject constructor(
    private val getPolicyValidUseCase: GetTimeSlotPolicyValidUseCase,
) {
    private var dragMinsAcc = 0

    private fun normalize(minutesOfDay: Int): Int {
        return LocalTimeUtil.normalize(minutesOfDay, 15)
    }

    fun adjustSlotList(
        targetSlotId: String,
        slotMinuteFactor: Int,
        currentList: List<TimeSlotItemUiState>,
        changeType: TimeSlotChangeTimeType
    ): List<TimeSlotItemUiState> {
        val targetItem = currentList.find { it.slotUuid == targetSlotId }
        if(targetItem == null) {
            dragMinsAcc = 0
            return currentList
        }

        val newStart = normalize(targetItem.startMinutesOfDay + dragMinsAcc)
        val newEnd = normalize(targetItem.endMinutesOfDay + dragMinsAcc)
        dragMinsAcc = if (targetItem.startMinutesOfDay == newStart)
            dragMinsAcc + slotMinuteFactor
        else 0

        val updatedItem = when (changeType) {
            TimeSlotChangeTimeType.START_TIME -> targetItem.copy(startMinutesOfDay = newStart)
            TimeSlotChangeTimeType.END_TIME -> targetItem.copy(endMinutesOfDay = newEnd)
            TimeSlotChangeTimeType.START_AND_END -> targetItem.copy(
                startMinutesOfDay = newStart,
                endMinutesOfDay = newEnd
            )
        }

        val policyResult = getPolicyValidUseCase.invoke(
            TimeSlotVO(
                startTime = LocalTimeUtil.create(updatedItem.startMinutesOfDay),
                endTime = LocalTimeUtil.create(updatedItem.endMinutesOfDay),
                title = updatedItem.title
            )
        )
        if (policyResult !is DomainResult.Success) return currentList

        val result = if (changeType == TimeSlotChangeTimeType.START_AND_END)
            currentList.swapSlotList(updatedItem)
        else
            currentList.update(updatedItem)

        val normalized = result.flatMap {
            it.copy(
                startMinutesOfDay = LocalTimeUtil.create(it.startMinutesOfDay).asMinutes(),
                endMinutesOfDay = LocalTimeUtil.create(it.endMinutesOfDay).asMinutes()
            ).splitOverMidnight().map { split ->
                split.copy(isSelected = split.slotUuid == updatedItem.slotUuid)
            }
        }.distinct()

        return normalized
    }
}

private fun List<TimeSlotItemUiState>.swapSlotList(
    updateItem: TimeSlotItemUiState,
): List<TimeSlotItemUiState> {

    val overlapItem = this.getOverlapItem(updateItem) ?: return this.update(updateItem)

    val updateSourceTime: TimeSlotItemUiState = this.find {
        updateItem.slotUuid == it.slotUuid
    } ?: return this
    val updateItemMinutes = updateItem.run { this.endMinutesOfDay - this.startMinutesOfDay }

    val overlapItemMinutes =
        overlapItem.run { this.endMinutesOfDay - this.startMinutesOfDay }

    val newUpdateItem: TimeSlotItemUiState
    val newOverlapItem: TimeSlotItemUiState
    when {
        updateSourceTime.midMinute() > updateItem.midMinute() -> {
            //down to up.
            Timber.d("down to up.")
            newUpdateItem = updateSourceTime.copy(
                startMinutesOfDay = overlapItem.startMinutesOfDay,
                endMinutesOfDay = overlapItem.startMinutesOfDay + updateItemMinutes
            )
            newOverlapItem = overlapItem.copy(
                startMinutesOfDay = updateSourceTime.endMinutesOfDay - overlapItemMinutes,
                endMinutesOfDay = updateSourceTime.endMinutesOfDay,
            )
        }

        updateSourceTime.midMinute() < updateItem.midMinute() -> {
            //up to down
            Timber.d("up to down.")
            newUpdateItem = updateSourceTime.copy(
                startMinutesOfDay = overlapItem.endMinutesOfDay - updateItemMinutes,
                endMinutesOfDay = overlapItem.endMinutesOfDay
            )
            newOverlapItem = overlapItem.copy(
                startMinutesOfDay = updateSourceTime.startMinutesOfDay,
                endMinutesOfDay = updateSourceTime.startMinutesOfDay + overlapItemMinutes,
            )
        }

        else -> {
            newUpdateItem = updateSourceTime
            newOverlapItem = overlapItem
        }
    }

    Timber.d("timeslot order changed. newUpdateItem=${newUpdateItem.timeLog()}, newOverlapItem=${newOverlapItem.timeLog()}")

    return this.map {
        when (it.slotUuid) {
            newOverlapItem.slotUuid -> newOverlapItem
            newUpdateItem.slotUuid -> newUpdateItem
            else -> it
        }
    }
}

private fun List<TimeSlotItemUiState>.update(
    updateItem: TimeSlotItemUiState,
): List<TimeSlotItemUiState> {
    val overlapItem = this.getOverlapItem(updateItem) ?: //중복 없음. 업데이트.
    return this.map {
        if (it.slotUuid == updateItem.slotUuid) updateItem
        else it
    }

    val updateOriginItem = this.find { it.slotUuid == updateItem.slotUuid } ?: return this

    //update와 updateOrigin의 중앙값을 비교하여, 진행 방향 확인.
    val adjustedItem = when {
        updateItem.midMinute() < updateOriginItem.midMinute() -> {
            //아래에서 위로.
            // overlap의 아래쪽 끝에 닿지 않도록, overlap 바로 뒤로 이동
            updateItem.copy(
                startMinutesOfDay = overlapItem.endMinutesOfDay,
            )
        }

        // update가 overlap 위쪽(즉 더 이른 시간대)에 위치
        updateItem.midMinute() > updateOriginItem.midMinute() -> {
            // overlap의 위쪽 끝에 닿지 않도록, overlap 바로 위로 이동
            updateItem.copy(
                endMinutesOfDay = overlapItem.startMinutesOfDay
            )
        }

        // 완전히 겹침 (update가 overlap을 완전히 덮음)
        else -> {
            //오류이므로, 그냥 update무시.
            null
        }
    } ?: return this


    return this.map {
        if (it.slotUuid == adjustedItem.slotUuid) adjustedItem
        else it
    }
}

private fun List<TimeSlotItemUiState>.getOverlapItem(
    updateItem: TimeSlotItemUiState,
): TimeSlotItemUiState? = find {
    if (updateItem.slotUuid == it.slotUuid) return@find false
    else {
        LocalTimeUtil.overlab(
            updateItem.startMinutesOfDay % LocalTimeUtil.DAY_MINUTES until updateItem.endMinutesOfDay % LocalTimeUtil.DAY_MINUTES,
            it.startMinutesOfDay % LocalTimeUtil.DAY_MINUTES until it.endMinutesOfDay % LocalTimeUtil.DAY_MINUTES
        )
    }
}
