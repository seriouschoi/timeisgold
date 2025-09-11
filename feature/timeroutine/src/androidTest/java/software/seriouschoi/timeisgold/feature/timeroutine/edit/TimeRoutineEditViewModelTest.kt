package software.seriouschoi.timeisgold.feature.timeroutine.edit

import app.cash.turbine.test
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.test.runTest
import org.junit.Before
import software.seriouschoi.timeisgold.core.android.test.util.toSavedStateHandle
import software.seriouschoi.timeisgold.core.test.util.FakeTimeRoutineRepositoryAdapter
import software.seriouschoi.timeisgold.core.test.util.TimeRoutineTestFixtures
import software.seriouschoi.timeisgold.domain.services.TimeRoutineDomainService
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.DeleteTimeRoutineUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.GetTimeRoutineDefinitionUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.GetValidTimeRoutineUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.SetTimeRoutineUseCase
import software.seriouschoi.timeisgold.feature.timeroutine.fake.FakeDestNavigatorPortAdapter
import java.time.DayOfWeek
import kotlin.test.Test

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
            getTimeRoutineUseCase = GetTimeRoutineDefinitionUseCase(
                timeRoutineRepositoryPort = routineAdapter,
            ),
            setTimeRoutineUseCase = SetTimeRoutineUseCase(
                timeRoutineRepositoryPort = routineAdapter,
                timeRoutineDomainService = TimeRoutineDomainService(
                    timeRoutineRepository = routineAdapter
                ),
            ),
            getValidTimeRoutineUseCase = GetValidTimeRoutineUseCase(
                service = TimeRoutineDomainService(
                    timeRoutineRepository = routineAdapter
                ),
            ),
            deleteTimeRoutineUseCase = DeleteTimeRoutineUseCase(
                timeRoutineRepositoryPort = routineAdapter,
            ),
            savedStateHandle = TimeRoutineEditScreenRoute(
                testDayOfWeek.ordinal
            ).toSavedStateHandle()
        )

    }

    @Test
    fun readRoutine_showRoutineEdit() = runTest {
        viewModel.init()
        viewModel.uiStateFlow.filter {
            it is TimeRoutineEditUiState.Routine
        }.test {
            val item = awaitItem()

            assert(item is TimeRoutineEditUiState.Routine) {
                "루틴 편집 화면 진입 실패. item=$item"
            }
        }
    }

    @Test
    fun readRoutine_readFailed_showRoutineEdit() = runTest {
        routineAdapter.flags = FakeTimeRoutineRepositoryAdapter.Flags(
            readRoutine = false
        )
        viewModel.init()

        viewModel.uiStateFlow.filter {
            it is TimeRoutineEditUiState.Routine
        }.test {
            val item = awaitItem()
            assert(item is TimeRoutineEditUiState.Routine) {
                "루틴 편집 화면 진입 실패. item=$item"
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

        viewModel.uiStateFlow.filter {
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