package software.seriouschoi.timeisgold.core.common.ui.components

import androidx.compose.runtime.Composable
import java.time.LocalTime

/**
 * Created by jhchoi on 2025. 11. 2.
 * jhchoi
 */
@Composable
fun TigTimePicker(time: LocalTime, onChangeTime: (LocalTime) -> Unit) {

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