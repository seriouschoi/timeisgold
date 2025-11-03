package software.seriouschoi.timeisgold.core.common.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import software.seriouschoi.timeisgold.core.common.ui.TigThemePreview
import java.time.LocalTime

/**
 * Created by jhchoi on 2025. 11. 2.
 * jhchoi
 */
@Composable
fun TigTimePicker(
    time: LocalTime,
    modifier: Modifier = Modifier,
    onChangeTime: (LocalTime) -> Unit
) {
    Row(
        modifier = modifier
    ) {
        //hour
        TigNumberPickerView(
            value = time.hour,
            range = 0..23,
        ) {
            onChangeTime.invoke(LocalTime.of(it, time.minute))
        }

        //minute
        TigNumberPickerView(
            value = time.minute,
            range = 0..59,
        ) {
            onChangeTime.invoke(LocalTime.of(time.hour, it))
        }
    }
}

@Composable
@TigThemePreview
private fun Preview() {
    TigTimePicker(
        time = LocalTime.of(10, 30),
    ) {

    }
}