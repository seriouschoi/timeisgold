package software.seriouschoi.timeisgold.domain.repositories

import software.seriouschoi.timeisgold.domain.data.timeslot.TimeSlotDetailData

interface TimeSlotRepository {
    suspend fun addTimeSlot(timeSlotData: TimeSlotDetailData, timeRoutineUuid: String)
    suspend fun getTimeSlotDetail(timeslotUuid: String): TimeSlotDetailData?
    suspend fun setTimeSlot(timeSlotData: TimeSlotDetailData)
    suspend fun deleteTimeSlot(timeslotUuid: String)
}
