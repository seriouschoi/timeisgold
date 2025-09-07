package software.seriouschoi.timeisgold.core.common.util

import kotlin.coroutines.cancellation.CancellationException

/**
 * 공용 예외처리 함수.
 */
suspend inline fun <T> runSuspendCatching(crossinline block: suspend () -> T): Result<T> =
    try {
        val result = block()
        Result.success(result)
    } catch (e: CancellationException) {
        // cancel은 던짐.
        throw e
    } catch (t: Throwable) {
        // 그외 에러는 catch.
        Result.failure(t)
    }

