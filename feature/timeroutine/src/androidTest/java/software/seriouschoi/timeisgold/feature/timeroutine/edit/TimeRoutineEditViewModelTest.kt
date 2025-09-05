package software.seriouschoi.timeisgold.feature.timeroutine.edit

import software.seriouschoi.timeisgold.core.android.test.util.toSavedStateHandle
import software.seriouschoi.timeisgold.core.test.util.FakeTimeRoutineRepositoryAdapter
import software.seriouschoi.timeisgold.core.test.util.TimeRoutineTestFixtures
import software.seriouschoi.timeisgold.domain.services.TimeRoutineDomainService
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.GetTimeRoutineUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.SetTimeRoutineUseCase
import software.seriouschoi.timeisgold.feature.timeroutine.fake.FakeDestNavigatorPortAdapter
import java.time.DayOfWeek
import kotlin.test.Test

/**
 * Created by jhchoi on 2025. 9. 4.
 * jhchoi
 */
class TimeRoutineEditViewModelTest {

    private val testDayOfWeek = DayOfWeek.MONDAY

    private val testFixture = listOf(
        TimeRoutineTestFixtures().routineCompoMonTue
    )
    private val routineAdapter = FakeTimeRoutineRepositoryAdapter(
        mockTimeRoutines = testFixture
    )

    private val viewModel = TimeRoutineEditViewModel(
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

    /**
     * 뷰모델 초기화 확인.
     */
    @Test
    fun test() {
        //todo 이거 테스트 해봐야함.
        val state = viewModel.uiState.value as? TimeRoutineEditUiState.Routine
        assert(state?.currentDayOfWeek == testDayOfWeek)
    }
}