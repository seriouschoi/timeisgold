package software.seriouschoi.timeisgold.feature.timeroutine.presentation.dayofweeks

internal data class DayOfWeeksUiState(
    val isLoading: Boolean = false,
    val dayOfWeekList: List<DayOfWeekItemUiState> = emptyList()
)