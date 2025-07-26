package software.seriouschoi.timeisgold.domain.repositories

import software.seriouschoi.timeisgold.domain.data.TimeSlotData
import software.seriouschoi.timeisgold.domain.data.TimeSlotDetailData

interface TimeSlotRepository {
    suspend fun getTimeSlotList(): List<TimeSlotData>
    suspend fun addTimeSlot(timeSlotData: TimeSlotDetailData)
    suspend fun setTimeSlot(timeSlotData: TimeSlotDetailData)
    suspend fun getTimeSlot(timeslotUuid: String): TimeSlotDetailData?
    suspend fun deleteTimeSlot(timeslotUuid: String)
}
