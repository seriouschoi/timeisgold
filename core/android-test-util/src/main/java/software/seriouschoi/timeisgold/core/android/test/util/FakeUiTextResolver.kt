package software.seriouschoi.timeisgold.core.android.test.util

import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.core.common.ui.provider.UiTextResolver

/**
 * Created by jhchoi on 2025. 9. 15.
 * jhchoi
 */
class FakeUiTextResolver : UiTextResolver {
    override fun getString(uiText: UiText): String {
        return when(uiText) {
            is UiText.Raw -> uiText.value
            else -> {
                //굳이 실제 리소스를 리턴하지 않아도 됨. 객체의 변경 여부만 판단해도 문제 없음.
                "[FAKE] $uiText"
            }
        }
    }
}