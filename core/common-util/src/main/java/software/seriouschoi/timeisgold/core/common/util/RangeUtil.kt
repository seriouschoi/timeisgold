package software.seriouschoi.timeisgold.core.common.util

/**
 * Created by jhchoi on 2025. 11. 5.
 * jhchoi
 */
object RangeUtil {
    fun generateCircularRange(
        start: Int,
        end: Int,
        bound: Int
    ): List<Int> {
        return generateSequence(start) {
            (it + 1) % bound
        }.takeWhile {
            it != end
        }.plus(end).toList()
    }
}
