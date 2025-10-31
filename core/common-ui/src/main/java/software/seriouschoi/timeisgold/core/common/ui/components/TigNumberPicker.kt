package software.seriouschoi.timeisgold.core.common.ui.components

import android.widget.NumberPicker
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Created by jhchoi on 2025. 10. 21.
 * jhchoi
 */
@Composable
fun TigNumberPickerView(
    value: Int,
    range: IntRange,
    modifier: Modifier = Modifier,
    onValueChange: (Int) -> Unit
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            NumberPicker(context).apply {
                minValue = range.first
                maxValue = range.last
                this.value = value
                setOnValueChangedListener { _, _, newVal ->
                    onValueChange(newVal)
                }
            }
        },
        update = { picker ->
            if (picker.value != value) {
                picker.value = value
            }
        }
    )
}