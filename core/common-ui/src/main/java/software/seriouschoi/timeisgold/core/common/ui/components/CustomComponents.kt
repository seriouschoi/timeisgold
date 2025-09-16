package software.seriouschoi.timeisgold.core.common.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import software.seriouschoi.timeisgold.core.common.ui.TigTheme
import software.seriouschoi.timeisgold.core.common.ui.TigThemePreview
import software.seriouschoi.timeisgold.core.common.ui.container.TigBlurContainer

@TigThemePreview
@Composable
private fun ComponentsPreview() {
    TigTheme {
        TigBlurContainer {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Text("Just TextView.")
                TigCircleText(
                    "13"
                )
                TigSingleLineTextField(
                    value = "TigSingleLine\nTextField",
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth()
                )
                TigSingleLineTextField(
                    hint = "TigSingleLineTextFieldHint",
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth()
                )
                TigCheckButton(label = "TigCheckButton", checked = true, onCheckedChange = {})
                TigLabelButton(
                    onClick = {},
                    label = "TigLabelButton"
                )
                TigLabelButton(
                    onClick = {},
                    label = "TigLabelButton disabled",
                    enabled = false
                )
                TigBottomBar(modifier = Modifier) {
                    TigLabelButton(
                        onClick = {},
                        label = "TigLabelButton1"
                    )

                    TigLabelButton(
                        onClick = {},
                        label = "TigLabelButton2"
                    )
                }
                TigCircleProgress()
            }
        }
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
            Text(text = message)

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
        Text(text = label)
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
fun TigSingleLineTextField(
    modifier: Modifier = Modifier,
    value: String = "",
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
            Text(
                text = hint,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(
                    onClick = {
                        onValueChange("")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                    )
                }
            }
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
        Text(text = label)
    }
}

@Composable
fun TigCircleText(
    text: String,
    size: Dp = 48.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}
