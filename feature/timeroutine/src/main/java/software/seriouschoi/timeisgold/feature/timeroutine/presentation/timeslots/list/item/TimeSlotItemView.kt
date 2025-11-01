package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.item

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import software.seriouschoi.timeisgold.core.common.ui.TigTheme
import software.seriouschoi.timeisgold.core.common.ui.TigThemePreview
import software.seriouschoi.timeisgold.core.common.ui.times.TimePixelUtil
import kotlin.math.abs
import software.seriouschoi.timeisgold.core.common.ui.R as CommonR

@Composable
internal fun TimeSlotItemCardView(
    modifier: Modifier,
    item: TimeSlotItemUiState,
    globalPositioned: Modifier = Modifier,
    hourHeight: Dp
) {
    val density = LocalDensity.current
    val hourHeightPx = density.run { hourHeight.toPx() }
    val topOffsetPx = TimePixelUtil.minutesToPx(item.startMinutesOfDay.toLong(), hourHeightPx)
    val slotHeightPx =
        abs(item.endMinutesOfDay - item.startMinutesOfDay).let {
            TimePixelUtil.minutesToPx(it.toLong(), hourHeightPx)
        }

    val topOffset = density.run { topOffsetPx.toDp() }
    val slotHeight = density.run { slotHeightPx.toDp() }

    val cardColor = if (item.isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainer
    }
    Card(
        modifier = modifier
            .padding(start = 40.dp)
            .height(slotHeight)
            .offset(
                x = 0.dp,
                y = topOffset
            )
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
                val unTitle = stringResource(CommonR.string.text_untitle)
                Text(
                    text = item.title.takeIf { it.isNotEmpty() } ?: unTitle,
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = item.getStartTimeText(),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = item.getEndTimeText(),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@TigThemePreview
@Composable
private fun Preview() {
    TigTheme {
        Box {
            TimeSlotItemCardView(
                modifier = Modifier.fillMaxWidth(),
                item = TimeSlotItemUiState(
                    slotUuid = "",
                    title = "",
                    startMinutesOfDay = 0,
                    endMinutesOfDay = 60,
                    isSelected = false
                ),
                globalPositioned = Modifier,
                hourHeight = 60.dp
            )
        }
    }
}