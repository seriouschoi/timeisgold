package software.seriouschoi.timeisgold.data.repositories

import android.database.sqlite.SQLiteConstraintException
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import software.seriouschoi.timeisgold.data.BaseRoomTest
import software.seriouschoi.timeisgold.data.fixture.TimeSlotTestFixtures
import timber.log.Timber
import java.time.LocalTime

@RunWith(AndroidJUnit4::class)
internal class TimeSlotRepositoryImplTest : BaseRoomTest() {
    private lateinit var repo: TimeSlotRepositoryImpl
    private val timeSlotTestFixtures = TimeSlotTestFixtures

    @Before
    fun setup() {
        Timber.d("setup")
        repo = TimeSlotRepositoryImpl(db)

        //other list add.
        runTest {
            timeSlotTestFixtures.createDetailDataList().forEach {
                repo.addTimeSlot(it)
            }
        }
    }

    @Test
    fun addTimeSlot_should_PersistEntityCorrectly() {
        runTest {
            val testData = timeSlotTestFixtures.createDetailDataList().first()
            repo.addTimeSlot(testData)

            val compareData = repo.getTimeSlot(testData.timeSlotData.uuid)
                ?: throw IllegalStateException("compare data is null. add time slot failed.")

            assert(compareData == testData)
        }
    }

    @Test
    fun addTimeSlot_withoutMemo_should_PersistEntityCorrectly() {
        runTest {
            val testData = timeSlotTestFixtures.createDetailTimeSlot().copy(
                timeSlotMemoData = null
            )
            repo.addTimeSlot(testData)

            val compareData = repo.getTimeSlot(testData.timeSlotData.uuid)
                ?: throw IllegalStateException("compare data is null. add time slot failed.")

            assert(compareData == testData)
        }
    }

    @Test(expected = SQLiteConstraintException::class)
    fun addTimeSlot_duplicateUuid_shouldThrowException() {
        runTest {
            val testData = timeSlotTestFixtures.createDetailTimeSlot()
            repo.addTimeSlot(testData)

            val testData2 =
                testData.copy(timeSlotData = testData.timeSlotData.copy())
            repo.addTimeSlot(testData2)
        }
    }

    @Test(expected = SQLiteConstraintException::class)
    fun addTimeSlot_duplicateMemoUuid_shouldThrowException() {
        runTest {
            val testData1Source = timeSlotTestFixtures.createDetailTimeSlot()
            val testData2 = timeSlotTestFixtures.createDetailTimeSlot()
            val testData2Memo = testData2.timeSlotMemoData?.copy()
            val testData1 = testData1Source.copy(
                timeSlotMemoData = testData2Memo
            )

            repo.addTimeSlot(testData2)
            repo.addTimeSlot(testData1)
        }
    }

    @Test
    fun setTimeSlot_should_PersistEntityCorrectly() {
        runTest {
            val allTimeSlotList = repo.getTimeSlotList().map {
                repo.getTimeSlot(it.uuid)
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
            repo.setTimeSlot(changedData)

            val compareData = repo.getTimeSlot(changedData.timeSlotData.uuid)
            assert(changedData == compareData)
        }
    }

    @Test
    fun setTimeSlot_withoutMemo_should_PersistEntityCorrectly() {
        runTest {
            val testData = timeSlotTestFixtures.createDetailTimeSlot().copy(
                timeSlotMemoData = null
            )
            repo.addTimeSlot(testData)

            val changedTimeSlot = testData.timeSlotData.copy(
                title = "test_title_changed"
            )
            val changedData = testData.copy(
                timeSlotData = changedTimeSlot
            )
            repo.setTimeSlot(changedData)

            val compareData = repo.getTimeSlot(changedData.timeSlotData.uuid)
            assert(changedData == compareData)
        }
    }

    @Test
    fun setTimeSlot_deleteMemo_should_PersistEntityCorrectly() {
        runTest {
            val allTimeSlotList = repo.getTimeSlotList().map {
                repo.getTimeSlot(it.uuid)
            }
            //데이터 변경.(메모 삭제)
            val testData =
                allTimeSlotList.first() ?: throw IllegalStateException("test data is null")
            val changedData = testData.copy(
                timeSlotMemoData = null
            )
            repo.setTimeSlot(changedData)

            //변경된 데이터가 잘 적용됐는가?
            val compareData = repo.getTimeSlot(changedData.timeSlotData.uuid)
            assert(changedData == compareData)
            assert(changedData.timeSlotMemoData == null)
        }
    }

    @Test
    fun deleteTimeSlot_should_DeletedTimeSlotAndMemo() {
        runTest {
            val timeSlotDetailList = repo.getTimeSlotList().map {
                repo.getTimeSlot(it.uuid)
            }

            //삭제.
            val timeSlot =
                timeSlotDetailList.first() ?: throw IllegalStateException("test data is null")
            repo.deleteTimeSlot(timeSlot.timeSlotData.uuid)

            //삭제된 정보를 조회.
            val compareData = repo.getTimeSlot(timeSlot.timeSlotData.uuid)
            assert(compareData == null)

            //다른 데이터는 잘 있는가?
            val otherTimeSlot =
                timeSlotDetailList[1] ?: throw IllegalStateException("test data is null")
            val compareOtherData = repo.getTimeSlot(otherTimeSlot.timeSlotData.uuid)
            assert(compareOtherData == otherTimeSlot)
        }
    }

    @Test
    fun getTimeSlotList_should_ReturnTimeSlotList() {
        runTest {
            //테스트를 위한 목록 추가.
            val timeSlotDetailList = timeSlotTestFixtures.createDetailDataList()
            timeSlotDetailList.forEach {
                repo.addTimeSlot(it)
            }

            val timeSlotList = timeSlotDetailList.map {
                it.timeSlotData
            }.sortedBy {
                it.uuid
            }

            //가져온 데이터에 새로 추가된 데이터가 있는가?
            val compareList = repo.getTimeSlotList().filter {
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
            val testData = timeSlotTestFixtures.createDetailTimeSlot()
            repo.addTimeSlot(testData)

            val compareData = repo.getTimeSlot(testData.timeSlotData.uuid)
            assert(compareData == testData)
        }
    }

    @Test
    fun getTimeSlot_withDeletedTimeSlot_should_ReturnNull() {
        runTest {
            //없는 데이터상태를 만들기 위해 기존 데이터중 하나를 삭제.
            val testData = repo.getTimeSlotList().first().let {
                repo.getTimeSlot(it.uuid)
            } ?: throw IllegalStateException("test data is null")
            repo.deleteTimeSlot(testData.timeSlotData.uuid)

            //없는 데이터를 요청하면 null로 리턴하는가?
            val compareData = repo.getTimeSlot(testData.timeSlotData.uuid)
            assert(compareData == null)
        }
    }

    @Test
    fun getTimeSlot_withDeletedTimeSlotMemo_should_ReturnOnlyTimeSlot() {
        runTest {
            //메모가 없는 데이터를 저장.
            val testData = repo.getTimeSlotList().first().let {
                repo.getTimeSlot(it.uuid)?.copy(
                    timeSlotMemoData = null
                )
            } ?: throw IllegalStateException("test data is null")
            repo.setTimeSlot(testData)


            val compareData = repo.getTimeSlot(testData.timeSlotData.uuid)
                ?: throw IllegalStateException("test data is null")
            assert(compareData.timeSlotData == testData.timeSlotData)
            assert(compareData.timeSlotMemoData == null)
        }
    }

}