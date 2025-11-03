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
            /*
             factory는 처음 구성될때만 1회 실행 됨.
             리컴포지션이 일어나서 갱신될 내용은 update에서 진행해야 함.
             예:
             setOnValueChangedListener를 factory에서 설정하게 되면,
             재구성 이전의 onValueChange 를 호출하는 문제가 발생 할 수 있음.
             */
            NumberPicker(context)
        },
        update = { picker ->
            if (picker.value != value) {
                picker.value = value
            }
            picker.minValue = range.first
            picker.maxValue = range.last
            picker.setOnValueChangedListener { _, _, newVal ->
                onValueChange(newVal)
            }
        }
    )
}