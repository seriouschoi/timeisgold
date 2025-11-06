package software.seriouschoi.timeisgold.core.common.ui.components

import android.widget.NumberPicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    onValueChange: (Int) -> Unit,
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
            NumberPicker(context).apply {
                this.updateValue(value, range)
            }
        },
        update = { picker ->
            picker.value = value
            picker.updateValue(value, range)
            picker.setOnValueChangedListener { _, _, newVal ->
                onValueChange(newVal)
            }
        }
    )
}

private fun NumberPicker.updateValue(value: Int, range: IntRange) {
    this.value = value
    this.minValue = range.first
    this.maxValue = range.last
}

@Composable
fun TigLabelPicker(
    labels: List<String>,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    onValueChange: (Int) -> Unit,
) {
    val itemCount = Integer.MAX_VALUE
    val startIndex = itemCount / 2 - (itemCount / 2 % labels.size) + selectedIndex
    val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = startIndex - 1)
    val snappingLayout = remember(lazyListState) { SnapLayoutInfoProvider(lazyListState) }
    val flingBehavior = rememberSnapFlingBehavior(snappingLayout)

    LaunchedEffect(selectedIndex) {
        val newIndex = itemCount / 2 - (itemCount / 2 % labels.size) + selectedIndex
        if (lazyListState.firstVisibleItemIndex + 1 != newIndex) {
            lazyListState.animateScrollToItem(newIndex - 1)
        }
    }

    LaunchedEffect(lazyListState.isScrollInProgress) {
        if (!lazyListState.isScrollInProgress) {
            val centerItemIndex = lazyListState.firstVisibleItemIndex + 1
            if (centerItemIndex < labels.size) {
                onValueChange(centerItemIndex)
            } else {
                onValueChange(centerItemIndex % labels.size)
            }
        }
    }

    
    LazyColumn(
        modifier = modifier,
        state = lazyListState,
        flingBehavior = flingBehavior,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(count = itemCount) { index ->
            val labelIndex = index % labels.size
            val label = labels[labelIndex]
            Box(
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth()
                    .clickable { onValueChange(labelIndex) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = if (labelIndex == selectedIndex) FontWeight.Bold else FontWeight.Normal
                    )
                )
            }
        }
    }
}

@Preview
@Composable
private fun TigLabelPickerPreview() {
    TigLabelPicker(
        labels = (0 until 12).toList().map { "$it" },
        selectedIndex = 50,
        modifier = Modifier.height(100.dp)
    ) {

    }
}