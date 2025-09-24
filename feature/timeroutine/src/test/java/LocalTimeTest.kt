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
        assert(LocalTime.of(0, 11).normalize() == LocalTime.of(0, 15))
        assert(LocalTime.of(0, 16).normalize() == LocalTime.of(0, 15))
        assert(LocalTime.of(0, 10).normalize() == LocalTime.of(0, 15))
        assert(LocalTime.of(0, 2).normalize() == LocalTime.of(0, 0))

    }
}