import software.seriouschoi.timeisgold.core.common.util.LocalTimeUtil
import software.seriouschoi.timeisgold.core.common.util.normalize
import java.time.LocalTime
import kotlin.test.Test

/**
 * Created by jhchoi on 2025. 9. 24.
 * jhchoi
 */
class LocalTimeTest {

    @Test
    fun localTimeAddMinuteTest() {
        val localTime = LocalTime.of(0, 0)
        val changedTime = localTime.plusMinutes(70)
        assert(changedTime == LocalTime.of(1, 10))
    }

    @Test
    fun localTimeNormalizeTest() {
        assert(LocalTime.of(0, 11).normalize(15) == LocalTime.of(0, 15))
        assert(LocalTime.of(0, 16).normalize(15) == LocalTime.of(0, 15))
        assert(LocalTime.of(0, 10).normalize(15) == LocalTime.of(0, 15))
        assert(LocalTime.of(0, 2).normalize(15) == LocalTime.of(0, 0))

    }

    @Test
    fun localTimeUtilCreateTest() {
        LocalTimeUtil.create(70).let {
            println(it)
            assert(it == LocalTime.of(1, 10))
        }
        LocalTimeUtil.create(-70).let {
            println(it)
            assert(it == LocalTime.of(22, 50))
        }
        LocalTimeUtil.create(-70, 30).let {
            println(it)
            assert(it == LocalTime.of(23, 0))
        }

        LocalTimeUtil.create(1450).let {
            println(it)
            assert(it == LocalTime.of(0, 10))
        }

        LocalTimeUtil.create(1450, 15).let {
            println(it)
            assert(it == LocalTime.of(0, 15))
        }
    }
}