package software.seriouschoi.timeisgold.core.common.ui.container

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
fun TigContainer(
    loading: Boolean = false,
    composable: @Composable () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    val blurModifier = Modifier
        .background(Color.Black.copy(alpha = 0.3f))
        .tapClearFocus(focusManager)
        .graphicsLayer {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val renderEffect = BlurEffect(
                    radiusX = 20f,
                    radiusY = 20f,
                    edgeTreatment = TileMode.Clamp
                )
                //require androidx.compose.ui.graphics
                this.renderEffect = renderEffect
            }
        }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(
                if (loading) blurModifier
                else Modifier
            )
    ) {
        composable()
    }
    if (loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    enabled = true,
                    onClick = {
                        //consume.
                    },
                    interactionSource = remember { MutableInteractionSource() }
                ),
            contentAlignment = Alignment.Center) {
            TigCircleProgress()
        }
    }
}