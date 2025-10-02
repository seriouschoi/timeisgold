package software.seriouschoi.timeisgold.core.common.ui.times

import kotlin.math.roundToLong

/**
 * Created by jhchoi on 2025. 10. 2.
 * 시간을 픽셀로 변환하는 함수.
 * jhchoi
 */
object TimePixelUtil {
    fun pxToMinutes(pixel: Long, hourHeightPx: Float): Long {
        return ((pixel / hourHeightPx) * 60).roundToLong()
    }

    fun minutesToPx(minutes: Long, hourHeightPx: Float): Float {
        return (minutes / 60f) * hourHeightPx
    }
}
