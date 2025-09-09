package software.seriouschoi.timeisgold.core.common.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

/**
 * Created by jhchoi on 2025. 8. 26.
 * jhchoi
 */
sealed class UiText {
    data class Res(@get:StringRes val id: Int, val args: List<Any> = emptyList()) : UiText() {
        companion object {
            fun create(@StringRes id: Int, vararg args: Any) = Res(id, args.toList())
        }
    }
    data class MultipleRes(@get:StringRes val id: Int, val args: List<Int> = emptyList()) : UiText() {
        companion object {
            fun create(@StringRes id: Int, vararg args: Int) = MultipleRes(id, args.toList())
        }
    }
    data class Raw(val value: String) : UiText()
}

@Composable
fun UiText.asString(): String {
    return when(this) {
        is UiText.Raw -> {
            this.value
        }
        is UiText.Res -> {
            stringResource(id, args)
        }

        is UiText.MultipleRes -> {
            val stringArgs = args.map {
                stringResource(it)
            }
            stringResource(id, *stringArgs.toTypedArray())
        }
    }
}