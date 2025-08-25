package software.seriouschoi.timeisgold

import android.util.Log

/**
 * Created by jhchoi on 2025. 8. 18.
 */


fun logd(msg: String) {
    Log.d("Unknown", msg)
}
fun Any.logd(msg: String) {
    val tag = this::class.simpleName ?: "Unknown"
    Log.d(tag, msg)
}