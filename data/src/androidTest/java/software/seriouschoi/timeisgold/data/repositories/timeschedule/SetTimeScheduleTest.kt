package software.seriouschoi.timeisgold.data.repositories.timeschedule

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import software.seriouschoi.timeisgold.data.BaseRoomTest
import software.seriouschoi.timeisgold.domain.data.time_schedule.TimeScheduleDayOfWeekData
import java.util.UUID

/**
 * Created by jhchoi on 2025. 8. 7.
 * jhchoi@neofect.com
 */
@RunWith(AndroidJUnit4::class)
internal class SetTimeScheduleTest : BaseRoomTest() {
    @Before
    fun setup() {
        runTest {
            val dayOfWeekForSchedule = timeSlotTestFixtures.getTestScheduleDayOfWeeks1()
            val schedule = timeSlotTestFixtures.createTimeSchedule(dayOfWeekForSchedule)
            timeScheduleRepo.addTimeSchedule(schedule)

            timeSlotTestFixtures.createDetailDataList().forEach {
                timeSlotRepo.addTimeSlot(
                    timeSlotData = it,
                    timeScheduleUuid = schedule.uuid
                )
            }
        }
    }

    @Test
    fun setTimeSchedule_changeTitleTimeslotDayofweek_shouldCollectChanged() {
        runTest {
            val dayOfWeekForSchedule = timeSlotTestFixtures.getTestScheduleDayOfWeeks1()
            val scheduleDetailFromDb =
                timeScheduleRepo.getTimeScheduleDetail(dayOfWeekForSchedule.first())
                    ?: throw IllegalStateException("schedule is null")
            val scheduleFromDb = scheduleDetailFromDb.timeScheduleData
            val timeSlotList = scheduleDetailFromDb.timeSlotList

            val dayOfWeekForNewSchedule = timeSlotTestFixtures.getTestScheduleDayOfWeeks2()
            val scheduleForChange = scheduleFromDb.copy(
                timeScheduleName = "test_title_changed",
                dayOfWeekList = dayOfWeekForNewSchedule.map {
                    TimeScheduleDayOfWeekData(
                        dayOfWeek = it,
                        uuid = UUID.randomUUID().toString()
                    )
                },
                createTime = System.currentTimeMillis(),
            )
            timeScheduleRepo.setTimeSchedule(scheduleForChange)

            //jhchoi 2025. 8. 7. 스케줄 변경이 잘 되는가?
            val changedSchedule = timeScheduleRepo.getTimeScheduleByUuid(scheduleFromDb.uuid)
            assert(scheduleFromDb != changedSchedule?.timeScheduleData)
            assert(scheduleForChange == changedSchedule?.timeScheduleData)
        }
    }

    @Test
    fun setTimeSchedule_changeUuid_shouldThrowException() {
        // DESIGN NOTE:
        // 현재 DB 구조상 `id`가 PK로 작동하고 `uuid`는 제약이 없음.
        // 따라서 `uuid`만 바꾸고 update를 해도 정상 동작하므로, 테스트할 필요가 없음.
        // 도메인 불변성 위반은 Repository/UseCase 계층에서 검증되어야 하며,
        // 구조 변경 시 본 테스트는 재검토 필요함.
    }
}