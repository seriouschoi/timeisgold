package software.seriouschoi.timeisgold.core.common.ui.provider

import android.content.Context
import software.seriouschoi.timeisgold.core.common.ui.UiText
import javax.inject.Inject

class UiTextProvider @Inject internal constructor(
    private val context: Context,
) {
    fun getString(uiText: UiText): String {
        return when (uiText) {
            is UiText.MultipleResArgs -> {
                val stringArgs = uiText.args.map {
                    context.getString(it)
                }
                context.getString(uiText.id, *stringArgs.toTypedArray())
            }

            is UiText.MultipleUiTextArgs -> {
                val stringArgs = uiText.args.filter {
                    it !is UiText.MultipleUiTextArgs
                }.map {
                    getString(it)
                }
                context.getString(uiText.id, *stringArgs.toTypedArray())
            }

            is UiText.Raw -> uiText.value
            is UiText.Res -> context.getString(uiText.id, uiText.args)
        }
    }
}