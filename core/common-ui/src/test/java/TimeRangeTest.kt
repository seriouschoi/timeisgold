import timber.log.Timber
import kotlin.test.Test
import kotlin.test.todo

/**
 * Created by jhchoi on 2025. 11. 5.
 * jhchoi
 */
class TimeRangeTest {

    @Test
    fun test() {
        //22 ~ 03 -> [22, 23, 00, 01, 02, 03
        val rangeData = 22 to 3

        val hours = generateRange(
            start = rangeData.first, end = rangeData.second, max = 24
        )
        Timber.d("hours = $hours")
    }

    @Test
    fun testLocalTime() {
        // TODO: jhchoi 2025. 11. 5. local time으로 위 내용을 다시 구현.
    }


}