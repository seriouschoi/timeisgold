package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.slotedit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import software.seriouschoi.timeisgold.core.common.ui.TigTheme
import software.seriouschoi.timeisgold.core.common.ui.components.TigNumberPickerView
import software.seriouschoi.timeisgold.core.common.ui.components.TigSingleLineTextField
import timber.log.Timber
import java.time.LocalTime
import software.seriouschoi.timeisgold.core.common.ui.R as CommonR

/**
 * Created by jhchoi on 2025. 10. 21.
 * jhchoi
 */
@Composable
internal fun TimeSlotEditView(
    modifier: Modifier = Modifier,
    state: TimeSlotEditState,
    sendIntent: (TimeSlotEditStateIntent) -> Unit = {},
) {
    Timber.d("state=$state")
    Column(
        modifier = modifier
    ) {
        TigSingleLineTextField(
            value = state.title,
            onValueChange = {
                sendIntent.invoke(
                    TimeSlotEditStateIntent.Update(
                        slotTitle = it,
                        slotId = state.slotUuid
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            hint = stringResource(
                id = CommonR.string.text_timeslot
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Spacer(
                modifier = Modifier.weight(1f)
            )
            TigNumberPickerView(
                value = state.startTime.hour,
                range = 0..23,
            ) {
                sendIntent.invoke(TimeSlotEditStateIntent.Update(
                    startTime = LocalTime.of(it, state.startTime.minute),
                    slotId = state.slotUuid
                ))
            }
            TigNumberPickerView(
                value = state.startTime.minute,
                range = 0..59,
            ) {
                sendIntent.invoke(TimeSlotEditStateIntent.Update(
                    startTime = LocalTime.of(state.startTime.hour, it),
                    slotId = state.slotUuid
                ))
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )

            TigNumberPickerView(
                value = state.endTime.hour,
                range = 0..23,
            ) {
                sendIntent.invoke(TimeSlotEditStateIntent.Update(
                    endTime = LocalTime.of(it, state.endTime.minute),
                    slotId = state.slotUuid
                ))
            }
            TigNumberPickerView(
                value = state.endTime.minute,
                range = 0..59,
            ) {
                sendIntent.invoke(TimeSlotEditStateIntent.Update(
                    endTime = LocalTime.of(state.endTime.hour, it),
                    slotId = state.slotUuid
                ))
            }
            Spacer(
                modifier = Modifier.weight(1f)
            )
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