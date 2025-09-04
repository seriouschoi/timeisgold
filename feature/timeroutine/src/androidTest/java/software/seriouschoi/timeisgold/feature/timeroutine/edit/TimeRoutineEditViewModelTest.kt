package software.seriouschoi.timeisgold.feature.timeroutine.edit

import software.seriouschoi.timeisgold.domain.services.TimeRoutineDomainService
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.GetTimeRoutineUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.SetTimeRoutineUseCase
import software.seriouschoi.timeisgold.feature.timeroutine.fake.FakeDestNavigatorPortAdapter
import software.seriouschoi.timeisgold.feature.timeroutine.fake.FakeTimeRoutineRepositoryPortAdapter
import software.seriouschoi.timeisgold.feature.timeroutine.fake.toSavedStateHandle
import java.time.DayOfWeek
import kotlin.test.Test
import kotlin.test.todo

/**
 * Created by jhchoi on 2025. 9. 4.
 * jhchoi
 */
class TimeRoutineEditViewModelTest {

    private val testDayOfWeek = DayOfWeek.MONDAY

    private val viewModel = TimeRoutineEditViewModel(
        navigator = FakeDestNavigatorPortAdapter,
        getTimeRoutineUseCase = GetTimeRoutineUseCase(
            timeRoutineRepositoryPort = FakeTimeRoutineRepositoryPortAdapter
        ),
        setTimeRoutineUseCase = SetTimeRoutineUseCase(
            timeRoutineRepositoryPort = FakeTimeRoutineRepositoryPortAdapter,
            timeRoutineDomainService = TimeRoutineDomainService(
                timeRoutineRepository = FakeTimeRoutineRepositoryPortAdapter
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