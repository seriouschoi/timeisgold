package software.seriouschoi.timeisgold.core.common.util

import org.junit.Test
import java.time.LocalTime

/**
 * Created by jhchoi on 2025. 12. 2.
 * jhchoi
 */
class LocalTimeUtilFindTimeTest {
    @Test
    fun test_findTimeTest_1() {
        val timeList = listOf(
            LocalTime.of(1, 0) to LocalTime.of(2, 0),
            LocalTime.of(2, 20) to LocalTime.of(4, 0),
            LocalTime.of(23, 0) to LocalTime.of(6, 0),
        )

        val testTime = LocalTime.of(2, 10)

        val expectedPreviousTime = timeList[0].second
        val expectedNextTime = timeList[1].first

        test_findTimeTest(
            timeList = timeList,
            testTime = testTime,
            expectedPreviousTime = expectedPreviousTime,
            expectedNextTime = expectedNextTime
        )
    }

    @Test
    fun test_findTimeTest_2() {
        val timeList = listOf(
            LocalTime.of(6, 0) to LocalTime.of(10, 0),
            LocalTime.of(10, 0) to LocalTime.of(14, 0),
            LocalTime.of(18, 0) to LocalTime.of(21, 0),
            LocalTime.of(21, 0) to LocalTime.of(22, 0),
        )


        val expectedPreviousTime = timeList[3].second
        val expectedNextTime = timeList[0].first

        val testTime1 = LocalTime.of(23, 0)
        test_findTimeTest(
            timeList = timeList,
            testTime = testTime1,
            expectedPreviousTime = expectedPreviousTime,
            expectedNextTime = expectedNextTime
        )

        val testTime2 = LocalTime.of(1, 0)
        test_findTimeTest(
            timeList = timeList,
            testTime = testTime2,
            expectedPreviousTime = expectedPreviousTime,
            expectedNextTime = expectedNextTime
        )

    }


    fun test_findTimeTest(
        timeList: List<Pair<LocalTime, LocalTime>>,
        testTime: LocalTime,
        expectedPreviousTime: LocalTime?,
        expectedNextTime: LocalTime?
    ) {
        val sorted = LocalTimeUtil.sortAndSplitOvernightList(timeList.map {
            it.first.asMinutes() to it.second.asMinutes()
        })

        val previousTime = LocalTimeUtil.findPreviousTime(
            sorted,
            testTime.asMinutes()
        )
        val nextTime = LocalTimeUtil.findNextTime(
            sorted,
            testTime.asMinutes()
        )
        val previousEndTime = previousTime?.second?.let {
            LocalTimeUtil.create(it)
        }
        val nextStartTime = nextTime?.first?.let {
            LocalTimeUtil.create(it)
        }

        assert(previousEndTime == expectedPreviousTime)
        assert(nextStartTime == expectedNextTime)
    }
}