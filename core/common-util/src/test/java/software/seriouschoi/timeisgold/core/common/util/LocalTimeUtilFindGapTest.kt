package software.seriouschoi.timeisgold.core.common.util

import org.junit.Test
import java.time.LocalTime

/**
 * Created by jhchoi on 2025. 12. 2.
 * jhchoi
 */
class LocalTimeUtilFindGapTest {
    @Test
    fun test_findGap_1() {
        val testSlots = listOf(
            LocalTime.of(1, 0) to LocalTime.of(2, 0),
            LocalTime.of(3, 0) to LocalTime.of(4, 0),
        )

        test_findGap(
            testSlots = testSlots,
            range = LocalTime.of(2, 0) to LocalTime.of(3, 0),
            expected = LocalTime.of(2, 0) to LocalTime.of(3, 0)
        )
    }

    @Test
    fun test_findGap_2() {
        val testSlots = listOf(
            LocalTime.of(1, 0) to LocalTime.of(2, 0),
            LocalTime.of(2, 20) to LocalTime.of(4, 0),
        )

        test_findGap(
            testSlots = testSlots,
            range = LocalTime.of(2, 0) to LocalTime.of(3, 0),
            expected = LocalTime.of(2, 0) to LocalTime.of(2, 20)
        )
    }

    @Test
    fun test_findGap_3() {
        val testSlots = listOf(
            LocalTime.of(1, 0) to LocalTime.of(2, 20),
            LocalTime.of(2, 50) to LocalTime.of(4, 0),
        )

        test_findGap(
            testSlots = testSlots,
            range = LocalTime.of(2, 0) to LocalTime.of(3, 0),
            expected = LocalTime.of(2, 20) to LocalTime.of(2, 50)
        )
    }

    @Test
    fun test_findGap_4() {
        val testSlots = listOf(
            LocalTime.of(1, 0) to LocalTime.of(2, 20),
            LocalTime.of(4, 0) to LocalTime.of(6, 0),
        )

        test_findGap(
            testSlots = testSlots,
            range = LocalTime.of(2, 0) to LocalTime.of(3, 0),
            expected = LocalTime.of(2, 20) to LocalTime.of(3, 0)
        )
    }

    fun test_findGap(
        testSlots: List<Pair<LocalTime, LocalTime>>,
        range: Pair<LocalTime, LocalTime>,
        expected: Pair<LocalTime, LocalTime>
    ) {
        val sorted = testSlots.map {
            it.first.asMinutes() to it.second.asMinutes()
        }.let {
            LocalTimeUtil.sortAndSplitOvernightList(it)
        }

        val found = LocalTimeUtil.findGap(
            sorted,
            range.let { it.first.asMinutes() to it.second.asMinutes() }
        )?.let {
            LocalTimeUtil.create(it.first) to LocalTimeUtil.create(it.second)
        }

        assert(found == expected)
    }
}