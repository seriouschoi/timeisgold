package software.seriouschoi.timeisgold.core.common.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import software.seriouschoi.timeisgold.core.common.ui.R
import software.seriouschoi.timeisgold.core.common.ui.components.TigBottomBar
import software.seriouschoi.timeisgold.core.common.ui.components.TigLabelButton
import software.seriouschoi.timeisgold.core.common.ui.components.TigSurface
import java.time.LocalTime
import java.util.UUID

/**
 * Created by jhchoi on 2025. 9. 18.
 * jhchoi
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TigTimePickerDialog(
    time: LocalTime,
    dialogId: String,
    onSelect: (LocalTime) -> Unit
) {
    var show by remember(dialogId) { mutableStateOf(true) }

    if (!show) {
        return
    }

    val pickerState = TimePickerState(
        initialHour = time.hour,
        initialMinute = time.minute,
        is24Hour = true
    )

    if(show) {
        BasicAlertDialog(
            onDismissRequest = {

            }
        ) {
            TigSurface {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    TimePicker(
                        state = pickerState,
                    )

                    TigBottomBar {
                        TigLabelButton(
                            label = stringResource(R.string.text_cancel),
                        ) {
                            show = false
                        }
                        TigLabelButton(
                            label = stringResource(R.string.text_confirm),
                            onClick = {
                                val selectedTime = LocalTime.of(pickerState.hour, pickerState.minute)
                                onSelect(selectedTime)
                                show = false
                            }
                        )
                    }
                }
            }
        }
    }
}