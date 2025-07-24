package software.seriouschoi.timeisgold.data.repositories

import software.seriouschoi.timeisgold.domain.data.TimeSlotData
import software.seriouschoi.timeisgold.domain.repositories.TimeSlotRepository
import javax.inject.Inject

internal class TimeSlotRepositoryImpl @Inject constructor(

) : TimeSlotRepository {
    override fun getTimeSlotList(): List<TimeSlotData> {
        TODO("Not yet implemented")
    }

    override fun setTimeSlot(timeSlotData: TimeSlotData) {
        TODO("Not yet implemented")
    }

    override fun getTimeSlot(uuid: String): TimeSlotData {
        TODO("Not yet implemented")
    }

    override fun deleteTimeSlot(uuid: String) {
        TODO("Not yet implemented")
    }
}