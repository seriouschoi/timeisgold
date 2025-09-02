package software.seriouschoi.timeisgold.core.common.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

/**
 * Created by jhchoi on 2025. 8. 26.
 * jhchoi
 */
sealed class UiText {
    data class Res(@StringRes val id: Int, val args: List<Any> = emptyList()) : UiText()
    data class Raw(val value: String) : UiText()
}

@Composable
fun UiText.asString(): String {
    return when(this) {
        is UiText.Raw -> this.value
        is UiText.Res -> stringResource(id, args)
    }
}