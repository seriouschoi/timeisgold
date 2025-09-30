package software.seriouschoi.timeisgold.core.common.ui

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource

/**
 * Created by jhchoi on 2025. 8. 26.
 * jhchoi
 */
sealed interface UiText {
    data class Res(@get:StringRes val id: Int, val args: List<String> = emptyList()) : UiText {
        companion object {
            fun create(@StringRes id: Int, vararg args: String) = Res(id, args.toList())
        }
    }

    data class MultipleResArgs(@get:StringRes val id: Int, val args: List<Int> = emptyList()) :
        UiText {
        companion object {
            fun create(@StringRes id: Int, vararg args: Int) = MultipleResArgs(id, args.toList())
        }
    }

    data class MultipleUiTextArgs(
        @get:StringRes val id: Int, val args: List<UiText> = emptyList()
    ) : UiText {
        companion object {
            fun create(@StringRes id: Int, vararg args: UiText) = MultipleUiTextArgs(id, args.toList())
        }
    }

    data class Raw(val value: String) : UiText
}

@Composable
fun UiText.asString(): String {
    val context = LocalContext.current
    return asString(context)
}

fun UiText.asString(context: Context): String {
    return when (this) {
        is UiText.Raw -> {
            this.value
        }

        is UiText.Res -> {
            context.getString(id, *args.toTypedArray())
        }

        is UiText.MultipleResArgs -> {
            val stringArgs = args.map {
                context.getString(it)
            }
            context.getString(id, *stringArgs.toTypedArray())
        }

        is UiText.MultipleUiTextArgs -> {
            val stringArgs = args.filter{
                it !is UiText.MultipleUiTextArgs
            }.map {
                it.asString(context)
            }
            context.getString(id, *stringArgs.toTypedArray())
        }
    }
}