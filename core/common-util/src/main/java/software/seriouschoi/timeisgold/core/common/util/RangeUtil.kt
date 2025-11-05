package software.seriouschoi.timeisgold.core.common.util

/**
 * Created by jhchoi on 2025. 11. 5.
 * jhchoi
 */
object RangeUtil {
    fun generateRange(
        start: Int,
        end: Int,
        max: Int
    ): List<Int> {
        return generateSequence(start) {
            (it + 1) % max
        }.takeWhile {
            it != end
        }.plus(end).toList()
    }
}