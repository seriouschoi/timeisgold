package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.tables

import androidx.compose.runtime.Composable
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.TimeSlotItemUiState

// TODO:
@Composable
internal fun TimeSlotTableView(
    state: TimeSlotTableState
) {

}

internal data class TimeSlotTableState(
    val slotItemList: List<TimeSlotItemUiState> = emptyList(),
)
