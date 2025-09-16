package software.seriouschoi.timeisgold.core.common.ui.container

import android.os.Build
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import software.seriouschoi.timeisgold.core.common.ui.components.TigCircleProgress
import software.seriouschoi.timeisgold.core.common.ui.components.tapClearFocus

/**
 * Created by jhchoi on 2025. 9. 12.
 * jhchoi
 */
@Composable
fun TigBlurContainer(
    enableBlur: Boolean = false,
    blurOverlayContent: @Composable () -> Unit = {
        TigCircleProgress()
    },
    content: @Composable () -> Unit
) {
    val animationTime = 300
    val maxBlurRadius = 20f
    val dimAlpha by animateFloatAsState(
        targetValue = if (enableBlur) 0.3f else 0f,
        animationSpec = tween(durationMillis = animationTime), // 0.3초 페이드
        label = "LoadingDimAlpha"
    )

    val blurAlpha by animateFloatAsState(
        targetValue = if (enableBlur) maxBlurRadius else 0f,
        animationSpec = tween(durationMillis = animationTime), // 0.3초 페이드
        label = "LoadingBlurAlpha"
    )

    val focusManager = LocalFocusManager.current
    val blurModifier = Modifier.Companion
        .graphicsLayer {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val renderEffect = BlurEffect(
                    radiusX = blurAlpha,
                    radiusY = blurAlpha,
                    edgeTreatment = TileMode.Companion.Clamp
                )
                //require androidx.compose.ui.graphics
                this.renderEffect = renderEffect
            }
        }

    Box(
        modifier = Modifier.Companion
            .fillMaxSize()
            .tapClearFocus(focusManager)
            .then(
                if (enableBlur || blurAlpha > 0f) blurModifier
                else Modifier.Companion
            )
    ) {
        content()
    }
    if (enableBlur || dimAlpha > 0f) {
        Box(
            modifier = Modifier.Companion
                .fillMaxSize()
                .clickable(
                    enabled = true,
                    onClick = {
                        //consume.
                    }
                )
                .then(
                    Modifier.Companion.background(Color.Companion.Black.copy(alpha = dimAlpha))
                ),
            contentAlignment = Alignment.Companion.Center) {
            blurOverlayContent()
        }
    }
}