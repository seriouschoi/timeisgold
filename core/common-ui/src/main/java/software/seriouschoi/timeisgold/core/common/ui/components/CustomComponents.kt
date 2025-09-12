package software.seriouschoi.timeisgold.core.common.ui.components

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import software.seriouschoi.timeisgold.core.common.ui.container.TigContainer

/**
 * Created by jhchoi on 2025. 9. 4.
 * jhchoi
 */
@Preview
@Composable
private fun ComponentsPreview() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TigText("test")
        TigSingleLineTextField(value = "test\naset", onValueChange = {})
        TigCheckButton(label = "test", checked = true, onCheckedChange = {})
        TigLabelButton(
            onClick = {},
            label = "enabled button"
        )
        TigLabelButton(
            onClick = {},
            label = "disabled button",
            enabled = false
        )
        TigBottomBar(modifier = Modifier) {
            TigLabelButton(
                onClick = {},
                label = "button1"
            )

            TigLabelButton(
                onClick = {},
                label = "button2"
            )
        }
        TigCircleProgress()
    }
}

@Preview
@Composable
private fun LoadingBoxPreview() {
    TigContainer(
        loading = true
    ) {
        ComponentsPreview()
    }
}

fun Modifier.tapClearFocus(focusManager: FocusManager): Modifier {
    return this.pointerInput(Unit) {
        detectTapGestures {
            focusManager.clearFocus(force = true)
        }
    }
}

@Preview
@Composable
private fun TigAlertPreview() {
    TigAlert(
        message = "message",
        confirmButtonText = "confirm",
        onClickConfirm = {},
        cancelButtonText = "cancel",
        onClickCancel = {},
        alertId = "alertId"
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TigAlert(
    alertId: String,
    message: String,
    confirmButtonText: String,
    onClickConfirm: () -> Unit,
    cancelButtonText: String? = null,
    onClickCancel: (() -> Unit)? = null,
) {
    var show by remember(alertId) { mutableStateOf(true) }

    if (!show) {
        return
    }

    BasicAlertDialog(
        onDismissRequest = {
//            show = false
        },
    ) {
        Column {
            TigText(text = message)

            TigBottomBar {
                if (onClickCancel != null && cancelButtonText != null) {
                    TigLabelButton(
                        onClick = {
                            onClickCancel()
                            show = false
                        },
                        label = cancelButtonText,
                    )
                }

                TigLabelButton(
                    onClick = {
                        onClickConfirm()
                        show = false
                    },
                    label = confirmButtonText,
                )
            }
        }
    }
}



@Composable
fun TigCircleProgress() {
    CircularProgressIndicator()
}

@Composable
fun TigLabelButton(
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    TigButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    ) {
        TigText(text = label)
    }
}

@Composable
fun TigButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    Button(onClick = onClick, modifier = modifier, enabled = enabled) {
        content()
    }
}

@Composable
fun TigBottomBar(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        content()
    }
}

@Composable
fun TigText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
    )
}

@Composable
fun TigSingleLineTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    hint: String = "",
) {
    val focusManager = LocalFocusManager.current
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        singleLine = true,
        maxLines = 1,
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus(force = true)
            }
        ),
        placeholder = {
            TigText(hint)
        }
    )
}

@Composable
fun TigCheckButton(
    modifier: Modifier = Modifier,
    label: String,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    enabled: Boolean = true,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
        TigText(text = label)
    }
}