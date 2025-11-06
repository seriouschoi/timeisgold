package software.seriouschoi.timeisgold.core.common.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import software.seriouschoi.timeisgold.core.common.util.RangeUtil
import java.time.LocalTime

/**
 * Created by jhchoi on 2025. 11. 2.
 * jhchoi
 */
@Composable
fun TigTimePicker(
    time: LocalTime,
    modifier: Modifier = Modifier,
    timeRange: Pair<LocalTime, LocalTime> = LocalTime.MIN to LocalTime.MAX,
    onChangeTime: (LocalTime) -> Unit
) {
    var currentTime by remember {
        mutableStateOf(time)
    }

    LaunchedEffect(currentTime) {
        if (currentTime.hour != time.hour || currentTime.minute != time.minute)
            onChangeTime.invoke(currentTime)
    }

    val hours = RangeUtil.generateCircularRange(
        start = timeRange.first.hour,
        end = timeRange.second.hour,
        bound = 24
    )

    val mins = when (currentTime.hour) {
        timeRange.first.hour -> {
            timeRange.first.minute to 59
        }

        timeRange.second.hour -> {
            0 to timeRange.second.minute
        }

        else -> {
            0 to 59
        }
    }.let {
        IntRange(it.first, it.second)
    }.toList()

    val hourIndex = hours.indexOf(currentTime.hour).takeIf { it >= 0 } ?: 0
    val minIndex = mins.indexOf(currentTime.minute).takeIf { it >= 0 } ?: 0

    Row(
        modifier = modifier.height(120.dp)
    ) {
        Spacer(
            modifier = Modifier.weight(1f)
        )
        TigLabelPicker(
            labels = hours.map {
                "$it"
            },
            selectedIndex = hourIndex,
            modifier = Modifier.weight(1f)
        ) {
            currentTime = LocalTime.of(it, currentTime.minute)
        }

        Spacer(
            modifier = Modifier.weight(1f)
        )

        TigLabelPicker(
            labels = mins.map {
                "$it"
            },
            selectedIndex = minIndex,
            modifier = Modifier.weight(1f)
        ) {
            currentTime = LocalTime.of(currentTime.hour, it)
        }

        Spacer(
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
@Preview
private fun Preview() {
    TigTimePicker(
        timeRange = LocalTime.of(0, 0) to LocalTime.of(23, 59),
        time = LocalTime.of(1, 59),
        modifier = Modifier
            .height(120.dp)
            .fillMaxWidth()
    ) {

    }
}