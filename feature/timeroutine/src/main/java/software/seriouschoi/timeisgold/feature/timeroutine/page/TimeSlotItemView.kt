package software.seriouschoi.timeisgold.feature.timeroutine.page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import software.seriouschoi.timeisgold.core.common.ui.TigTheme

@Composable
internal fun TimeSlotItemCardView(
    modifier: Modifier,
    item: TimeSlotItemUiState,
    heightDp: Dp,
    topOffsetPx: Int,
    gestureModifier: Modifier = Modifier,
    globalPositioned: Modifier = Modifier
) {
    val density = LocalDensity.current
    val cardColor = if (item.isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainer
    }
    Card(
        modifier = modifier
            .offset(
                x = 0.dp,
                y = density.run { topOffsetPx.toDp() }
            )
            .height(heightDp)
            .padding(start = 40.dp)
            .then(gestureModifier)
            .then(globalPositioned),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Box(Modifier.fillMaxSize()) {
            //content
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = item.getStartTimeText(),
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = item.getEndTimeText(),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    TigTheme {
        TimeSlotItemCardView(
            modifier = Modifier
                .fillMaxWidth(),
            item = TimeSlotItemUiState(
                slotUuid = "slotUuid",
                routineUuid = "routineUuid",
                title = "title",
                startMinutesOfDay = 0,
                endMinutesOfDay = 60,
                slotClickIntent = TimeRoutinePageUiIntent.ShowSlotEdit(
                    slotId = "slotUuid",
                    routineId = "routineUuid"
                ),
                isSelected = false,
            ),
            heightDp = 50.dp,
            topOffsetPx = 0,
        )
    }
}

private fun Float.pxToMinutes(hourHeightPx: Float): Float {
    return (this / hourHeightPx) * 60
}

private fun Int.minutesToPx(hourHeightPx: Float): Float {
    return (this / 60f) * hourHeightPx
}

private enum class DragTarget { Card, Top, Bottom }