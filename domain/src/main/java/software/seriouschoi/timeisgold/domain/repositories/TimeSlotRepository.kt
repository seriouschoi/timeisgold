package software.seriouschoi.timeisgold.domain.repositories

import software.seriouschoi.timeisgold.domain.data.TimeSlotData

interface TimeSlotRepository {
    fun getTimeSlotList(): List<TimeSlotData>
    fun setTimeSlot(timeSlotData: TimeSlotData)
    fun getTimeSlot(uuid: String): TimeSlotData
    fun deleteTimeSlot(uuid: String)
}
