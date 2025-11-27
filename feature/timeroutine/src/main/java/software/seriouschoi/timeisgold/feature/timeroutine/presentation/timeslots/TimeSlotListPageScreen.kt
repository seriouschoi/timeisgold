package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import software.seriouschoi.timeisgold.core.common.ui.TigTheme
import software.seriouschoi.timeisgold.core.common.ui.TigThemePreview
import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.core.common.ui.asString
import software.seriouschoi.timeisgold.core.common.util.MetaEnvelope
import software.seriouschoi.timeisgold.core.common.util.asMinutes
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.TimeSlotListState
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.TimeSlotListView
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.item.TimeSlotItemUiState
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.slotedit.TimeSlotEditState
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.slotedit.TimeSlotEditView
import java.time.DayOfWeek
import java.time.LocalTime
import software.seriouschoi.timeisgold.core.common.ui.R as CommonR

/**
 * Created by jhchoi on 2025. 8. 26.
 * jhchoi
 */
@Composable
fun TimeSlotListPageScreen(
    dayOfWeek: DayOfWeek,
    isCurrentPage: Boolean,
) {
    val viewModel = hiltViewModel<TimeSlotListPageViewModel>(key = dayOfWeek.name)

    val uiState by viewModel.uiState.collectAsState()

    val uiEvent by viewModel.uiEvent.collectAsState(null)

    LaunchedEffect(dayOfWeek) {
        viewModel.load(dayOfWeek)
    }

    DisposableEffect(dayOfWeek) {
        onDispose {
            viewModel.sendIntent(TimeSlotListPageUiIntent.SlotEditCancel)
        }
    }

    /*
    [BackHandler 이슈 요약]
    문제:
    Pager 내에서 BackHandler를 사용하면,
    현재 보이지 않는 미리 그려진(pre-composed) 페이지의 BackHandler가 뒤로가기 이벤트를 가로채는 현상 발생.
    이는 Composable 계층 구조상 가장 나중에 그려진 BackHandler가 우선권을 갖기 때문이다.

    해결:
    Pager로부터 현재 페이지 여부를 나타내는 `isCurrentPage` 상태를 전달받아,
    `BackHandler(enabled = isCurrentPage)` 와 같이 설정.
    이를 통해 현재 화면에 보이는 페이지의 BackHandler만 활성화하여 문제를 해결함.
     */
    BackHandler(enabled = isCurrentPage) {
        viewModel.sendIntent(TimeSlotListPageUiIntent.SlotEditCancel)
    }

    val snackBarHostState = remember { SnackbarHostState() }

    StateView(uiState, snackBarHostState) {
        viewModel.sendIntent(it)
    }
    EventView(uiEvent, snackBarHostState)
}

@Composable
private fun StateView(
    uiState: TimeSlotListPageUiState,
    snackBarHostState: SnackbarHostState,
    sendIntent: (TimeSlotListPageUiIntent) -> Unit
) {
    val editSlotState = uiState.editState
    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        content = { innerPadding ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                ListStateView(uiState.slotListState) {
                    sendIntent.invoke(it)
                }
            }
        },
        bottomBar = {
            if (editSlotState != null) {
                EditStateView(state = editSlotState, sendIntent = {
                    sendIntent.invoke(it)
                })
            }
        }
    )
}

@Composable
private fun EditStateView(
    state: TimeSlotEditState,
    sendIntent: (TimeSlotListPageUiIntent) -> Unit
) {
    Card {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                IconButton(onClick = {
                    sendIntent.invoke(
                        TimeSlotListPageUiIntent.SlotEditCancel
                    )
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(CommonR.string.text_close)
                    )
                }

                if (state.slotUuid != null) {
                    IconButton(onClick = {
                        sendIntent.invoke(
                            TimeSlotListPageUiIntent.DeleteTimeSlot(
                                slotId = state.slotUuid
                            )
                        )
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(CommonR.string.text_delete)
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth()
            )

            TimeSlotEditView(
                state = state,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                onChangeTitle = {
                    sendIntent.invoke(
                        TimeSlotListPageUiIntent.ChangeSelectedTimeSlotTitle(it)
                    )
                },
                onChangeStartTime = {
                    sendIntent.invoke(
                        TimeSlotListPageUiIntent.ChangeSelectedTimeSlotStartTime(it)
                    )
                },
                onChangeEndTime = {
                    sendIntent.invoke(
                        TimeSlotListPageUiIntent.ChangeSelectedTimeSlotEndTime(it)
                    )
                }
            )
        }
    }
}

@Composable
private fun EventView(
    event: MetaEnvelope<TimeSlotListPageUiEvent>?,
    snackBar: SnackbarHostState,
) {
    val context = LocalContext.current
    LaunchedEffect(event) {
        when (val payload = event?.payload) {
            is TimeSlotListPageUiEvent.ShowToast -> {

                val message = payload.message.asString(context)
                snackBar.showSnackbar(message)
            }

            null -> {
                //no work.
            }
        }
    }
}

@Composable
private fun ListStateView(
    currentState: TimeSlotListState,
    sendIntent: (TimeSlotListPageUiIntent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TimeSlotListView(
            slotItemList = currentState.slotItemList,
            modifier = Modifier.fillMaxSize()
        ) {
            sendIntent.invoke(it)
        }
    }

    if (currentState.loadingMessage != null) {
        Loading(currentState.loadingMessage)
    }

    if (currentState.errorMessage != null) {
        Error(currentState.errorMessage)
    }
}

@Composable
@TigThemePreview
private fun PreviewStateView() {
    val startTime = LocalTime.of(1, 30)
    val endTime = LocalTime.of(4, 20)

    val sampleSlotList = listOf(
        TimeSlotItemUiState(
            slotUuid = "temp_uuid",
            title = "Some Slot Title",
            startMinutesOfDay = startTime.asMinutes(),
            endMinutesOfDay = endTime.asMinutes(),
            isSelected = false,
        ),
    )
    TigTheme {
        StateView(
            uiState = TimeSlotListPageUiState(
                slotListState = TimeSlotListState(
                    sampleSlotList
                ),
                editState = TimeSlotEditState(
                    startTime = startTime,
                    endTime = endTime,
                    title = "test"
                ),
            ),
            snackBarHostState = remember { SnackbarHostState() }
        ) {

        }
    }
}

@TigThemePreview
@Composable
private fun PreviewSlotList() {
    TigTheme {
        val startTime = LocalTime.of(1, 30)
        val endTime = LocalTime.of(4, 20)
        TimeSlotListView(
            slotItemList = listOf(
                TimeSlotItemUiState(
                    slotUuid = "temp_uuid",
                    title = "Some Slot Title",
                    startMinutesOfDay = startTime.asMinutes(),
                    endMinutesOfDay = endTime.asMinutes(),
                    isSelected = false,
                ),
            ),
        ) {

        }
    }
}

@Composable
private fun Error(
    errorMessage: UiText,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.surfaceContainer
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = errorMessage.asString())
    }
}

@Composable
@Preview
private fun PreviewError() {
    Error(
        UiText.Res.create(CommonR.string.message_format_routine_create_confirm, "월요일")
    )
}


@Composable
private fun Loading(loadingMessage: UiText?) {
    if (loadingMessage != null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = loadingMessage.asString())
        }
    }
}

@Preview
@Composable
private fun PreviewLoading() {
    Loading(
        UiText.MultipleResArgs.create(
            CommonR.string.message_format_loading,
            CommonR.string.text_routine
        )
    )
}

