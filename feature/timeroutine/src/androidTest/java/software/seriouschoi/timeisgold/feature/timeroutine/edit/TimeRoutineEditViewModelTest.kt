package software.seriouschoi.timeisgold.feature.timeroutine.edit

import app.cash.turbine.test
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
        viewModel.uiState.test {
            var item = awaitItem()
            assert(item == TimeRoutineEditUiState.Loading) {
                "시작 상태가 로딩이 아님. item=$item"
            }
            viewModel.init()

            item = awaitItem()
            assert((item as? TimeRoutineEditUiState.Routine)?.routineUuid != null) {
                "루틴 블러오기 실패. item=$item"
            }
        }
    }

    @Test
    fun readRoutine_readFailed_showNewRoutine() = runTest {
        viewModel.uiState.test {
            routineAdapter.flags = FakeTimeRoutineRepositoryAdapter.Flags(
                readRoutine = false
            )
            skipItems(1)

            viewModel.init()
            Timber.d("")

            val item = awaitItem()
            assert(item is TimeRoutineEditUiState.Routine) {
                "루틴을 가져오지 못함. item=$item"
            }
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
    fun test() = runTest {

    }
}