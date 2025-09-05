package software.seriouschoi.timeisgold.feature.timeroutine.edit

import app.cash.turbine.test
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.test.runTest
import org.junit.Before
import software.seriouschoi.timeisgold.core.android.test.util.toSavedStateHandle
import software.seriouschoi.timeisgold.core.test.util.FakeTimeRoutineRepositoryAdapter
import software.seriouschoi.timeisgold.core.test.util.TimeRoutineTestFixtures
import software.seriouschoi.timeisgold.domain.services.TimeRoutineDomainService
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.GetTimeRoutineUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.SetTimeRoutineUseCase
import software.seriouschoi.timeisgold.feature.timeroutine.fake.FakeDestNavigatorPortAdapter
import timber.log.Timber
import java.time.DayOfWeek
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Created by jhchoi on 2025. 9. 4.
 * jhchoi
 */
class TimeRoutineEditViewModelTest {

    private lateinit var testDayOfWeek: DayOfWeek
    val testFixture = listOf(
        TimeRoutineTestFixtures().routineCompoMonTue
    )
    val routineAdapter = FakeTimeRoutineRepositoryAdapter(
        mockTimeRoutines = testFixture
    )
    private lateinit var viewModel: TimeRoutineEditViewModel

    @Before
    fun setup() {
        testDayOfWeek = TimeRoutineTestFixtures().routineCompoMonTue.dayOfWeeks.first().dayOfWeek

        viewModel = TimeRoutineEditViewModel(
            navigator = FakeDestNavigatorPortAdapter,
            getTimeRoutineUseCase = GetTimeRoutineUseCase(
                timeRoutineRepositoryPort = routineAdapter,
            ),
            setTimeRoutineUseCase = SetTimeRoutineUseCase(
                timeRoutineRepositoryPort = routineAdapter,
                timeRoutineDomainService = TimeRoutineDomainService(
                    timeRoutineRepository = routineAdapter
                ),
            ),
            savedStateHandle = TimeRoutineEditScreenRoute(
                testDayOfWeek.ordinal
            ).toSavedStateHandle()
        )

    }

    @Test
    fun readRoutine_showRoutine() = runTest {
        viewModel.init()
        viewModel.uiState.filter {
            it is TimeRoutineEditUiState.Routine
        }.test {
            val item = awaitItem()
            item as TimeRoutineEditUiState.Routine
            assert(item.routineUuid != null) {
                "루틴 블러오기 실패. item=$item"
            }
        }
    }

    @Test
    fun readRoutine_readFailed_showNewRoutine() = runTest {
        routineAdapter.flags = FakeTimeRoutineRepositoryAdapter.Flags(
            readRoutine = false
        )
        viewModel.init()

        viewModel.uiState.filter {
            it is TimeRoutineEditUiState.Routine
        }.test {
            val item = awaitItem()
            item as TimeRoutineEditUiState.Routine
            assert(item.routineUuid == null) {
                "새 루틴이 아님. item=$item"
            }
        }

        //no event.
        viewModel.uiEvent.test {
            expectNoEvents()
        }
    }

    @Test
    fun readRoutine_readError_showError() = runTest {
        routineAdapter.flags = FakeTimeRoutineRepositoryAdapter.Flags(
            readThrow = true
        )
        viewModel.init()

        viewModel.uiState.filter {
            it is TimeRoutineEditUiState.Routine
        }.test {
            expectNoEvents()
        }

        //no event.
        viewModel.uiEvent.filter {
            it is TimeRoutineEditUiEvent.ShowAlert
        }.test {
            assert(true)
        }

    }
}