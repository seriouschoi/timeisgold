package software.seriouschoi.timeisgold.core.common.util

/**
 * Created by jhchoi on 2025. 11. 5.
 * jhchoi
 */
object RangeUtil {
    /**
     * @param start 시작 값
     * @param end 종료 값
     * @param bound 범위
     */
    fun generateCircularRange(
        start: Int,
        end: Int,
        bound: Int,
        step: Int = 1
    ): List<Int> {
        return generateSequence(start) {
            (it + step) % bound
        }.takeWhile {
            it != end
        }.plus(end).toList()
    }
}
