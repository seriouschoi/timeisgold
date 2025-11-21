package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots

import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import software.seriouschoi.testutil.MainDispatcherRule
import software.seriouschoi.timeisgold.domain.usecase.timeslot.DeleteTimeSlotUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.NormalizeMinutesForUiUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.SetTimeSlotListUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.SetTimeSlotUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.WatchTimeSlotListUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.valid.GetTimeSlotPolicyValidUseCase
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.TimeSlotListStateHolder
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.logic.TimeSlotCalculator
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.slotedit.TimeSlotEditStateHolder
import kotlin.test.todo

/**
 * Created by jhchoi on 2025. 11. 20.
 * jhchoi
 */
class TimeSlotListPageViewModelTest {
    // TODO: jhchoi 2025. 11. 20. 테스트 작성.

    private lateinit var timeSlotEditStateHolder: TimeSlotEditStateHolder
    private lateinit var timeSlotListStateHolder: TimeSlotListStateHolder

    private lateinit var timeSlotCalculator: TimeSlotCalculator

    // TODO: jhchoi 2025. 11. 20. 6개 usecase의 모킹이라.. 뭔가 잘못됐다.
    /*
    테스트를 진행하며, 이것들을 좀 별도 계층으로 만들어서, 해당 계층에 의존하게 만들어야...
     */
    private lateinit var normalizeMinutesForUiUseCase: NormalizeMinutesForUiUseCase
    private lateinit var getPolicyValidUseCase: GetTimeSlotPolicyValidUseCase
    private lateinit var deleteTimeSlotUseCase: DeleteTimeSlotUseCase
    private lateinit var setTimeSlotUseCase: SetTimeSlotUseCase
    private lateinit var setTimeSlotsUseCase: SetTimeSlotListUseCase
    private lateinit var watchTimeSlotListUseCase: WatchTimeSlotListUseCase


    private lateinit var viewModel: TimeSlotListPageViewModel

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    @Before
    fun setup() {

        timeSlotEditStateHolder = TimeSlotEditStateHolder()
        timeSlotListStateHolder = TimeSlotListStateHolder()


        deleteTimeSlotUseCase = mock()
        setTimeSlotUseCase = mock()
        setTimeSlotsUseCase = mock()
        watchTimeSlotListUseCase = mock()

        timeSlotCalculator = TimeSlotCalculator(
            normalizeMinutesForUiUseCase = normalizeMinutesForUiUseCase,
            getPolicyValidUseCase = getPolicyValidUseCase
        )

        // TODO: jhchoi 2025. 11. 20. whenever로 모킹 구현.

        viewModel = TimeSlotListPageViewModel(
            watchTimeSlotListUseCase = watchTimeSlotListUseCase,
            setTimeSlotsUseCase = setTimeSlotsUseCase,
            setTimeSlotUseCase = setTimeSlotUseCase,
            deleteTimeSlotUseCase = deleteTimeSlotUseCase,
            timeSlotListStateHolder = timeSlotListStateHolder,
            timeSlotEditStateHolder = timeSlotEditStateHolder,
            timeSlotCalculator = timeSlotCalculator
        )
    }

    @Test
    fun test() = runTest(dispatcherRule.dispatcher){
        // TODO: jhchoi 2025. 11. 20.
        /*
        인텐트 찾자.
         */

    }
}