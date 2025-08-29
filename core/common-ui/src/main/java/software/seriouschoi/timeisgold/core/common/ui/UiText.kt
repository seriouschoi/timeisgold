package software.seriouschoi.timeisgold.core.common.ui

import androidx.annotation.StringRes

/**
 * Created by jhchoi on 2025. 8. 26.
 * jhchoi
 */
sealed class UiText {
    data class Res(@StringRes val id: Int, val args: List<Any> = emptyList()) : UiText()
    data class Raw(val value: String) : UiText()
}