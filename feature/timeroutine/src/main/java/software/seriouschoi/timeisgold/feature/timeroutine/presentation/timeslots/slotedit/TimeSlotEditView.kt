package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.slotedit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import software.seriouschoi.timeisgold.core.common.ui.TigTheme
import software.seriouschoi.timeisgold.core.common.ui.components.TigNumberPickerView
import software.seriouschoi.timeisgold.core.common.ui.components.TigSingleLineTextField
import java.time.LocalTime
import software.seriouschoi.timeisgold.core.common.ui.R as CommonR

/**
 * Created by jhchoi on 2025. 10. 21.
 * jhchoi
 */
@Composable
internal fun TimeSlotEditView(
    modifier: Modifier = Modifier,
    state: TimeSlotEditState
) {
    Column(
        modifier = modifier
    ) {
        TigSingleLineTextField(
            value = state.title,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            hint = stringResource(
                id = CommonR.string.text_timeslot
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            TigNumberPickerView(
                value = state.startTime.hour,
                range = 0..23,
            ) {

            }
            TigNumberPickerView(
                value = state.startTime.minute,
                range = 0..59,
            ) {

            }

            TigNumberPickerView(
                value = state.endTime.hour,
                range = 0..23,
            ) {

            }
            TigNumberPickerView(
                value = state.endTime.minute,
                range = 0..59,
            ) {

            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    TigTheme {
        TimeSlotEditView(
            modifier = Modifier.fillMaxWidth(),
            state = TimeSlotEditState(
                slotUuid = "temp_uuid",
                title = "Some Slot Title",
                startTime = LocalTime.of(1, 30),
                endTime = LocalTime.of(4, 20),
            )
        )
    }
}