package software.seriouschoi.timeisgold.core.common.util

import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

/**
 * Created by jhchoi on 2025. 10. 14.
 * jhchoi
 */
fun DayOfWeek.asShortText(): String {
    return this.getDisplayName(TextStyle.SHORT, Locale.getDefault())
}