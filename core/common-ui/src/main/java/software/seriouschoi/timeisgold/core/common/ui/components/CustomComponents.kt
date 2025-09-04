package software.seriouschoi.timeisgold.core.common.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.tooling.preview.Preview

/**
 * Created by jhchoi on 2025. 9. 4.
 * jhchoi
 */
@Preview
@Composable
private fun Preview() {
    Column {
        TigText("test")
        TigTextField(value = "test", onValueChange = {})
        TigCheckButton(label = "test", checked = true, onCheckedChange = {})
        TigLabelButton(
            onClick = {},
            label = "button"
        )
        TigBottomBar(modifier = Modifier) {
            TigButton(onClick = {}) {
                TigText("button")
            }
        }
        TigCircleProgress()
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
    onClickCancel: (() -> Unit)? = null
) {
    var show by remember(alertId) { mutableStateOf(true) }

    if (!show) {
        return
    }

    BasicAlertDialog(
        onDismissRequest = {
            show = false
        }
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String
) {
    TigButton(
        onClick = onClick,
        modifier = modifier
    ) {
        TigText(text = label)
    }
}

@Composable
fun TigButton(onClick: () -> Unit, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Button(onClick = onClick, modifier = modifier) {
        content()
    }
}

@Composable
fun TigBottomBar(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
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
fun TigTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
    )
}

@Composable
fun TigCheckButton(
    modifier: Modifier = Modifier,
    label: String,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        TigText(text = label)
    }
}