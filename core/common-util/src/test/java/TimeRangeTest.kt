import org.junit.Test
import software.seriouschoi.timeisgold.core.common.util.RangeUtil
import java.time.LocalTime

/**
 * Created by jhchoi on 2025. 11. 5.
 * jhchoi
 */
class TimeRangeTest {

    @Test
    fun test() {
        //22 ~ 03 -> [22, 23, 00, 01, 02, 03
        val rangeData = 22 to 3

        val hours = RangeUtil.generateCircularRange(
            start = rangeData.first, end = rangeData.second, bound = 24
        )

        println("hours = $hours")
    }

    @Test
    fun testLocalTime() {
        val rangeTime = LocalTime.of(22, 30) to LocalTime.of(3, 20)
        val selectedTime = LocalTime.of(23, 10)

        val hours = RangeUtil.generateCircularRange(
            start = rangeTime.first.hour,
            end = rangeTime.second.hour,
            bound = 24
        )

        /*
        minutes
        선택된 시가 start hour라면.. start min ~ 59
        선택된 시가 end hour라면.. 00 ~ end min
         */
        val mins = when(selectedTime.hour) {
            rangeTime.first.hour -> {
                rangeTime.first.minute to 59
            }
            rangeTime.second.hour -> {
                0 to rangeTime.second.minute
            }
            else -> {
                0 to 59
            }
        }.let {
            IntRange(it.first, it.second)
        }.toList()

        println("hours = $hours")
        println("mins = $mins")
    }
}