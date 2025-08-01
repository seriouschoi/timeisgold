package software.seriouschoi.timeisgold.data.fixture

import software.seriouschoi.timeisgold.domain.data.TimeSlotData
import software.seriouschoi.timeisgold.domain.data.TimeSlotDetailData
import software.seriouschoi.timeisgold.domain.data.TimeSlotMemoData
import java.time.LocalTime
import java.util.UUID

object TimeSlotTestFixtures {
    fun createDetailDataList(): List<TimeSlotDetailData> {
        return (0..10).map { i ->
            val uuid = UUID.randomUUID()
            val memoUuid = UUID.randomUUID()
            val createTime = System.currentTimeMillis() - (i * 1000 * 60)
            val timeSlotData = TimeSlotData(
                uuid = uuid.toString(),
                title = "test-$uuid",
                startTime = LocalTime.now(),
                endTime = LocalTime.now(),
                createTime = createTime
            )
            val timeSlotMemoData = TimeSlotMemoData(
                uuid = memoUuid.toString(),
                memo = "test-$memoUuid",
                createTime = createTime
            )
            return@map TimeSlotDetailData(
                timeSlotData = timeSlotData,
                timeSlotMemoData = timeSlotMemoData
            )
        }
    }

    fun createDetailTimeSlot(): TimeSlotDetailData {
        val uuid = UUID.randomUUID()
        val memoUuid = UUID.randomUUID()
        val timeSlotData = TimeSlotData(
            uuid = uuid.toString(),
            title = "test_$uuid",
            startTime = LocalTime.now(),
            endTime = LocalTime.now(),
            createTime = System.currentTimeMillis()
        )
        val timeSlotMemoData = TimeSlotMemoData(
            uuid = memoUuid.toString(),
            memo = "test_$memoUuid",
            createTime = System.currentTimeMillis()
        )

        return TimeSlotDetailData(
            timeSlotData = timeSlotData,
            timeSlotMemoData = timeSlotMemoData
        )
    }

}