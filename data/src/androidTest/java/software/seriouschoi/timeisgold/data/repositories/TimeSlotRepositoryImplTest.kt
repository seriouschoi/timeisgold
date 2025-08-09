package software.seriouschoi.timeisgold.data.repositories

import android.database.sqlite.SQLiteConstraintException
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import software.seriouschoi.timeisgold.data.BaseRoomTest
import software.seriouschoi.timeisgold.data.fixture.TimeSlotTestFixtures
import software.seriouschoi.timeisgold.domain.repositories.TimeScheduleRepository
import timber.log.Timber
import java.time.DayOfWeek
import java.time.LocalTime

@RunWith(AndroidJUnit4::class)
internal class TimeSlotRepositoryImplTest : BaseRoomTest() {
    private lateinit var timeSlotRepo: TimeSlotRepositoryImpl
    private lateinit var timeScheduleRepo: TimeScheduleRepository
    private val timeSlotTestFixtures = TimeSlotTestFixtures

    private val testDayOfWeekList = listOf(
        DayOfWeek.MONDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.FRIDAY
    )

    @Before
    fun setup() {
        Timber.d("setup")
        timeSlotRepo = TimeSlotRepositoryImpl(db)
        timeScheduleRepo = TimeScheduleRepositoryImpl(db)

        //other list add.
        runTest {
            val schedule = timeSlotTestFixtures.createTimeSchedule(testDayOfWeekList)
            timeScheduleRepo.addTimeSchedule(schedule)

            timeSlotTestFixtures.createDetailDataList().forEach {
                timeSlotRepo.addTimeSlot(it, schedule.uuid)
            }
        }
    }

    @Test
    fun addTimeSlot_should_PersistEntityCorrectly() {
        runTest {
            val schedule = timeScheduleRepo.getTimeSchedule(testDayOfWeekList.first())
                ?: throw IllegalStateException("time schedule is null")

            val testTimeSlotDetail = timeSlotTestFixtures.createDetailDataList().first()
            timeSlotRepo.addTimeSlot(
                timeSlotData = testTimeSlotDetail,
                timeScheduleUuid = schedule.timeScheduleData.uuid
            )

            val compareData =
                timeSlotRepo.getTimeSlotDetail(timeslotUuid = testTimeSlotDetail.timeSlotData.uuid)
                    ?: throw IllegalStateException("compare data is null. add time slot failed.")

            assert(compareData == testTimeSlotDetail)
        }
    }

    @Test
    fun addTimeSlot_withoutMemo_should_PersistEntityCorrectly() {
        runTest {
            val schedule = timeScheduleRepo.getTimeSchedule(testDayOfWeekList.first())
                ?: throw IllegalStateException("time schedule is null")

            val testData = timeSlotTestFixtures.createDetailTimeSlot().copy(
                timeSlotMemoData = null
            )
            timeSlotRepo.addTimeSlot(
                timeSlotData = testData,
                timeScheduleUuid = schedule.timeScheduleData.uuid
            )

            val compareData = timeSlotRepo.getTimeSlotDetail(testData.timeSlotData.uuid)
                ?: throw IllegalStateException("compare data is null. add time slot failed.")

            assert(compareData == testData)
        }
    }

    @Test(expected = SQLiteConstraintException::class)
    fun addTimeSlot_duplicateUuid_shouldThrowException() {
        runTest {
            val schedule = timeScheduleRepo.getTimeSchedule(testDayOfWeekList.first())
                ?: throw IllegalStateException("time schedule is null")

            val testData = timeSlotTestFixtures.createDetailTimeSlot()
            timeSlotRepo.addTimeSlot(testData, schedule.timeScheduleData.uuid)

            //같은 uuid 의 다른 타임 슬롯 생성.
            val newTestData = timeSlotTestFixtures.createDetailTimeSlot()
            val newTestTimeSlot = newTestData.timeSlotData.copy(
                uuid = testData.timeSlotData.uuid
            )
            val testData2 = newTestData.copy(timeSlotData = newTestTimeSlot)
            timeSlotRepo.addTimeSlot(
                timeSlotData = testData2,
                timeScheduleUuid = schedule.timeScheduleData.uuid
            )
        }
    }

    @Test(expected = SQLiteConstraintException::class)
    fun addTimeSlot_duplicateMemoUuid_shouldThrowException() {
        runTest {
            val schedule = timeScheduleRepo.getTimeSchedule(testDayOfWeekList.first())
                ?: throw IllegalStateException("time schedule is null")

            val testData1Source = timeSlotTestFixtures.createDetailTimeSlot()
            val testData1Memo =
                testData1Source.timeSlotMemoData ?: throw IllegalStateException("test data is null")
            timeSlotRepo.addTimeSlot(
                timeSlotData = testData1Source,
                timeScheduleUuid = schedule.timeScheduleData.uuid
            )

            val testData2Source = timeSlotTestFixtures.createDetailTimeSlot()
            val testData2Memo = testData2Source.timeSlotMemoData?.copy(
                uuid = testData1Memo.uuid
            ) ?: throw IllegalStateException("test data is null")

            timeSlotRepo.addTimeSlot(
                timeSlotData = testData2Source.copy(
                    timeSlotMemoData = testData2Memo
                ),
                timeScheduleUuid = schedule.timeScheduleData.uuid
            )
        }
    }

    @Test
    fun setTimeSlot_should_PersistEntityCorrectly() {
        runTest {
            val schedule = timeScheduleRepo.getTimeSchedule(testDayOfWeekList.first())
                ?: throw IllegalStateException("time schedule is null")

            val allTimeSlotList = schedule.timeSlotList.map {
                timeSlotRepo.getTimeSlotDetail(it.uuid)
            }
            val testData =
                allTimeSlotList.first() ?: throw IllegalStateException("test data is null")

            val changedTimeSlot = testData.timeSlotData.copy(
                title = "test_title_changed",
                startTime = LocalTime.now().minusMinutes(10),
            )
            val changedData = testData.copy(
                timeSlotData = changedTimeSlot
            )
            timeSlotRepo.setTimeSlot(changedData)

            val compareData = timeSlotRepo.getTimeSlotDetail(changedData.timeSlotData.uuid)
            assert(changedData == compareData)
        }
    }

    @Test
    fun setTimeSlot_withoutMemo_should_PersistEntityCorrectly() {
        runTest {
            val schedule = timeScheduleRepo.getTimeSchedule(testDayOfWeekList.first())
                ?: throw IllegalStateException("time schedule is null")

            val testData = timeSlotTestFixtures.createDetailTimeSlot().copy(
                timeSlotMemoData = null
            )
            timeSlotRepo.addTimeSlot(
                timeSlotData = testData,
                timeScheduleUuid = schedule.timeScheduleData.uuid
            )

            val changedTimeSlot = testData.timeSlotData.copy(
                title = "test_title_changed"
            )
            val changedData = testData.copy(
                timeSlotData = changedTimeSlot
            )
            timeSlotRepo.setTimeSlot(changedData)

            val compareData = timeSlotRepo.getTimeSlotDetail(changedData.timeSlotData.uuid)
            assert(changedData == compareData)
        }
    }

    @Test
    fun setTimeSlot_deleteMemo_should_PersistEntityCorrectly() {
        runTest {
            val schedule = timeScheduleRepo.getTimeSchedule(testDayOfWeekList.first())
                ?: throw IllegalStateException("time schedule is null")

            val allTimeSlotList = schedule.timeSlotList.map {
                timeSlotRepo.getTimeSlotDetail(it.uuid)
            }
            //데이터 변경.(메모 삭제)
            val testData =
                allTimeSlotList.first() ?: throw IllegalStateException("test data is null")
            val changedData = testData.copy(
                timeSlotMemoData = null
            )
            timeSlotRepo.setTimeSlot(changedData)

            //변경된 데이터가 잘 적용됐는가?
            val compareData = timeSlotRepo.getTimeSlotDetail(changedData.timeSlotData.uuid)
            assert(changedData == compareData)
            assert(changedData.timeSlotMemoData == null)
        }
    }

    @Test
    fun deleteTimeSlot_should_DeletedTimeSlotAndMemo() {
        runTest {
            val schedule = timeScheduleRepo.getTimeSchedule(testDayOfWeekList.first())
                ?: throw IllegalStateException("time schedule is null")

            val timeSlotDetailList = schedule.timeSlotList.map {
                timeSlotRepo.getTimeSlotDetail(it.uuid)
            }

            //삭제.
            val timeSlot =
                timeSlotDetailList.first() ?: throw IllegalStateException("test data is null")
            timeSlotRepo.deleteTimeSlot(timeSlot.timeSlotData.uuid)

            //삭제된 정보를 조회.
            val compareData = timeSlotRepo.getTimeSlotDetail(timeSlot.timeSlotData.uuid)
            assert(compareData == null)

            //다른 데이터는 잘 있는가?
            val otherTimeSlot =
                timeSlotDetailList[1] ?: throw IllegalStateException("test data is null")
            val compareOtherData = timeSlotRepo.getTimeSlotDetail(otherTimeSlot.timeSlotData.uuid)
            assert(compareOtherData == otherTimeSlot)
        }
    }

    @Test
    fun getTimeSlotList_should_ReturnTimeSlotList() {
        runTest {
            val schedule = timeScheduleRepo.getTimeSchedule(testDayOfWeekList.first())
                ?: throw IllegalStateException("time schedule is null")

            //테스트를 위한 목록 추가.
            val timeSlotDetailList = timeSlotTestFixtures.createDetailDataList()
            timeSlotDetailList.forEach {
                timeSlotRepo.addTimeSlot(it, schedule.timeScheduleData.uuid)
            }

            val timeSlotList = timeSlotDetailList.map {
                it.timeSlotData
            }.sortedBy {
                it.uuid
            }

            val addTimeSlotAfterSchedule = timeScheduleRepo.getTimeScheduleByUuid(schedule.timeScheduleData.uuid)
                ?: throw IllegalStateException("test data is null")

            //가져온 데이터에 새로 추가된 데이터가 있는가?

            val compareList = addTimeSlotAfterSchedule.timeSlotList.filter {
                timeSlotList.contains(it)
            }.sortedBy {
                it.uuid
            }

            assert(timeSlotList == compareList)
        }
    }

    @Test
    fun getTimeSlot_should_ReturnTimeSlot() {
        runTest {

            val schedule = timeScheduleRepo.getTimeSchedule(testDayOfWeekList.first())
                ?: throw IllegalStateException("time schedule is null")

            val testData = timeSlotTestFixtures.createDetailTimeSlot()
            timeSlotRepo.addTimeSlot(
                timeSlotData = testData,
                timeScheduleUuid = schedule.timeScheduleData.uuid
            )

            val compareData = timeSlotRepo.getTimeSlotDetail(testData.timeSlotData.uuid)
            assert(compareData == testData)
        }
    }

    @Test
    fun getTimeSlot_withDeletedTimeSlot_should_ReturnNull() {
        runTest {
            //없는 데이터상태를 만들기 위해 기존 데이터중 하나를 삭제.
            val schedule = timeScheduleRepo.getTimeSchedule(testDayOfWeekList.first())
                ?: throw IllegalStateException("time schedule is null")
            val testData = schedule.timeSlotList.first().let {
                timeSlotRepo.getTimeSlotDetail(it.uuid)
            } ?: throw IllegalStateException("test data is null")
            timeSlotRepo.deleteTimeSlot(testData.timeSlotData.uuid)

            //없는 데이터를 요청하면 null로 리턴하는가?
            val compareData = timeSlotRepo.getTimeSlotDetail(testData.timeSlotData.uuid)
            assert(compareData == null)
        }
    }

    @Test
    fun getTimeSlot_withDeletedTimeSlotMemo_should_ReturnOnlyTimeSlot() {
        runTest {
            val schedule = timeScheduleRepo.getTimeSchedule(testDayOfWeekList.first())
                ?: throw IllegalStateException("time schedule is null")
            //메모가 없는 데이터를 저장.
            val testData = schedule.timeSlotList.first().let {
                timeSlotRepo.getTimeSlotDetail(it.uuid)?.copy(
                    timeSlotMemoData = null
                )
            } ?: throw IllegalStateException("test data is null")
            timeSlotRepo.setTimeSlot(testData)


            val compareData = timeSlotRepo.getTimeSlotDetail(testData.timeSlotData.uuid)
                ?: throw IllegalStateException("test data is null")
            assert(compareData.timeSlotData == testData.timeSlotData)
            assert(compareData.timeSlotMemoData == null)
        }
    }

    @Test
    fun addTimeSchedule_should_PersistEntityCorrectly() {
        runTest {
            val schedule = timeSlotTestFixtures.createTimeSchedule(
                listOf(DayOfWeek.SUNDAY)
            )
            timeScheduleRepo.addTimeSchedule(schedule)

            val scheduleFromDb = timeScheduleRepo.getTimeSchedule(DayOfWeek.SUNDAY)
            assert(scheduleFromDb?.timeScheduleData == schedule)
        }
    }
}