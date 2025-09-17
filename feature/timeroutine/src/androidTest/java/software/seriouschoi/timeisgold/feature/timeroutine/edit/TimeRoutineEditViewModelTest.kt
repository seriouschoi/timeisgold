package software.seriouschoi.timeisgold.feature.timeroutine.edit

import app.cash.turbine.test
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.test.runTest
import org.junit.Before
import software.seriouschoi.timeisgold.core.android.test.util.FakeUiTextResolver
import software.seriouschoi.timeisgold.core.android.test.util.toSavedStateHandle
import software.seriouschoi.timeisgold.core.test.util.FakeTimeRoutineRepositoryAdapter
import software.seriouschoi.timeisgold.core.test.util.TimeRoutineTestFixtures
import software.seriouschoi.timeisgold.domain.services.TimeRoutineDomainService
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.DeleteTimeRoutineUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.GetAllRoutinesDayOfWeeksUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.GetDayOfWeeksTypeUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.WatchTimeRoutineDefinitionUseCase
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
            getTimeRoutineUseCase = WatchTimeRoutineDefinitionUseCase(
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
            ).toSavedStateHandle(),

            getAllDayOfWeeksUseCase = GetAllRoutinesDayOfWeeksUseCase(
                timeRoutineRepository = routineAdapter
            ),
            getDayOfWeeksTypeUseCase = GetDayOfWeeksTypeUseCase(),
            uiTextProvider = FakeUiTextResolver(),
        )

    }

    @Test
    fun readRoutine_showRoutineEdit() = runTest {
        viewModel.uiStateFlow.filter { !it.isLoading } // 로딩 완료 상태만 통과
            .test {
                val item = awaitItem() // emit될 때까지 대기
                assert(!item.isLoading) {
                    "루틴 편집 화면 진입 실패. item=$item"
                }
            }
    }

    /**
     * 데이터를 불러오는데 실패하면, 새로운 데이터로 준비.
     */
    @Test
    fun readRoutine_readFailed_showRoutineEdit() = runTest {
        routineAdapter.flags = FakeTimeRoutineRepositoryAdapter.Flags(
            readRoutine = false
        )

        viewModel.uiStateFlow.filter {
            !it.isLoading
        }.test {
            val item = awaitItem()

            assert(!item.visibleDelete) {
                "새로운 루틴인데, 삭제 버튼이 출력되고 있음."
            }
            assert(!item.isLoading) {
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

        //no event.
        viewModel.uiEvent.filter {
            it.payload is TimeRoutineEditUiEvent.ShowAlert
        }.test {
            assert(true)
        }
    }
}