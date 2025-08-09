package software.seriouschoi.timeisgold.domain.policy

import software.seriouschoi.timeisgold.domain.data.timeslot.TimeSlotData
import software.seriouschoi.timeisgold.domain.exception.TIGException

class TimeSlotPolicy {
    fun checkCanAdd(
        timeSlotDataListForCompare: List<TimeSlotData>,
        timeSlotDataForAdd: TimeSlotData
    ) {
        //timeslot의 시간이 겹치는지 확인하는 로직.
        val isDuplicateTime = timeSlotDataListForCompare.any {
            timeSlotDataForAdd.startTime in (it.startTime..it.endTime)
            timeSlotDataForAdd.endTime in (it.startTime..it.endTime)
        }
        if (isDuplicateTime) {
            throw TIGException.TimeSlotConflict(timeSlotDataForAdd)
        }
    }

}
