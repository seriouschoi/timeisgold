package software.seriouschoi.timeisgold.presentation.navigation

import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder

interface DestNavigatorPort {
    // TODO: jhchoi 2025. 8. 20.
    /*
    내가 보기엔...이녀석을 core의 모듈로 빼는게 맞다.
    그리고, app, feature모듈들에서 :core:Navigator 모듈을 가져오고.
     */
    fun setControllerProvider(provider: (() -> NavHostController)?)

    //추상화된 화면 이동.
    fun navigate(
        presentationDest: Any,
        opts: NavOptionsBuilder.() -> Unit = {}
    )

    fun back()
}