# Scratch board.
당장 떠오른 것들을 적는 공간이다.  
이곳에 적은것들은 후에 정리과정에서 개별 문서로 이관된다.

# reduce
사전적인 의미: 줄이다, 축소하다

개발에선 보통 단순화하다라는 의미까지 포함해서 사용한다.
예시로 kotlin의 collection에는 아래와 같이 사용되는 reduce함수가 있다.
```kotlin
val numbers = listOf(1, 2, 3, 4, 5)
val sum = numbers.reduce { acc, value -> acc + value }
println(sum) // 15
```
위 코드에서 reduce는 고차함수로서, 컬렉션을 단일값으로 축약하는 함수로 동작한다.

그렇다면, 현행 TimeRoutineEditViewModel에서 reduce라는 이름으로 정의한 
uiState갱신을 사실 잘못된 패턴일 수도 있나?

*아니다. 연속된 intent에 의해 누적해서 하나의 현재 상태로 축약(reduce) 한다는 의미가 있기 때문이다.*

중요한 건 reduce는 순수함수여야 하며,
reduce과정에서 발생할 것으로 보이는 부수효과와 분리해야 한다는 것이다.

이렇게, 말이다.
```kotlin
_uiIntent.distinctUntilChanged().collect { intent ->
    _uiState.update {
        it.reduceIntent(intent)
    }
    handleIntentSideEffect(intent)
}
```
연속된 uiIntent에 의해 누적해서 하나의 uiState로 축약하고 있다.
그리고 사이드이펙트, event를 발행하거나, usecase등을 호출하는 동작은 
별개의 handleIntentSideEffect에서 수행하고 있다.

# intent를 뷰모델에서 flow로 정의해서 받는 이유.
간단하지만, intent를 뷰모델에서 받을때, sendIntent메소드로 받고,
뷰모델에서는 해당 intent의 처리를 바로 해줘도 큰 문제는 없다.
사용자가 단순히 이전/다음/확인/취소 같은 버튼만 누른다면 말이다.
하지만, 사용자는 키보드에 연속으로 입력하기도 하고,
체크박스를 여러개를 빠르게 누르기도 한다. 
이런 intent가 뷰모델에서 모든 요구사항을 처리할 수 있다면, 큰 문제는 안되곘지만,
문제는 많은 요구사항들은 사용자가 실시간으로 발행하는 intent에 실시간으로 usecase와 상호작용을 해야한다는 것이다.
대표적으로 입력값의 유효성 체크이다.
사용자가 실컷 입력하고, 확인 버튼을 눌러야 쓸수 있다면,
참 불편하다.
그래서 intent를 뷰모델에서 flow로 받고, 입력폭주를 흡수하는 것이다.
그렇게 만들면, 연속적인 입력값에도 처리 가능한 만큼의 요청을 usecase에 전달하게 제어할 수 있다.


# getDataUseCase의 결과를 바로 uiState에 담지 않는다???
```kotlin
val uiState = getDataUseCase().asResultState().map {
    UiState(
        name = it.name,
        age = it.age,
        gender = it.gender
    )
}
```
이런식으로 정의하면 직관적이다.
하지만 이런식으로 하지 않게되는 이유가 있다.
왜냐하면, 뷰모델에서 받은 인텐트에 의해 uiState가 갱신될수도 있기 때문이다.
물론 이게 UDF를 해치는것 같기도 하다.
근데 UDF를 지키겠다고, 입력값을 모두 그 윗단까지 전파하는게 맞을까?
내가 생각하는 UDF는 ui의 입력에 의해 직접 ui를 바꾸는게 아니라, 뷰모델에서 입력을 처리하고,
그에 따른 uiState가 갱신되서 뷰는 uiState를 그리기만 할 뿐이라는 것이다.
물론 이 흐름이 uiState갱신 흐름이 지저분해지긴 하는데...

# Composition의 남용과 데이터 정의에 대한 고민.
## 부제: 데이터의 경계를 어떻게 할것인가?

TimeRoutineEdit에서 쓰는
SaveTimeRoutineUseCase와
GetValidTimeRoutineUseCase의 파라미터로
TimeRoutineEntity와 List<DayOfWeek>로 전달하고 있다.
현재는 겨우 두개의 파라미터지만,
이후 계획이 바뀌면, 두개의 UseCase에 파라미터를 계속 수정해야 하는
지옥이 펼쳐질 것이다.

문제는 이런 파라미터 호출구조가 된 이유는,
TimeRoutineComposition이라는
TimeRoutine과 TimeRoutineDayOfWeek, TimeSlot을
포함한 정의가 있는데,
TimeRoutine과 TimeRoutineDayOfWeek
이 두가지만 포함한 타입의 이름을 뭐라고 지을지랑,
이걸 만들었을때 저 Composition과 어떻게 구별할지가 불분명할 것이 우려되었다.

이문제를 해결하는 가장 좋은 방법은 데이터의 경계를 명확히 하는 것이다.
상술된 바와 같이 TimeRoutineEdit화면에서는
TimeRoutineEntity와 TimeRoutineDayOfWeekEntity만
사용한다.

즉 TimeRoutineComposition의 모든 요소를 쓰는게 아니다.
Composition은 일종의 데이터 관계정의이다.
사실 남용되어선 안되는 타입이다.

화면은 사실상 데이터의 다른 정의이기도 하다.
화면의 정의를 그대로 데이터로 옮기면,
TimeRoutine의 정의는
TimeRoutineEntity와 TimeRoutineDayOfWeekEntity로만
이뤄진다고 봐도 된다.

즉 데이터의 경계를 나누는 것이
이 문제를 해결하는데 가장 좋은 방법이다.
즉 TimeRoutineDefinition과 비슷한 다른 개념이 추가될 걸
걱정 하는 것은 기우이기도 하다.
그 경우에는 그 화면에 대한 정의로 타입을 새로 정의하면 된다.

# rememberSavable??

# SharedFlow, StateFlow, Flow???
1. SharedFlow
2. StateFlow
   1. 위 두개는 HotFlow
3. Flow
   1. 이건 ColdFlow

# build.gradle과 build.gradle.kts 차이???

# focus??

# Modifier에서 state란 이름으로 쓰는 패턴은 뭐지?

# 각 SharindStarted의 동작??
1. SharingStarted.Eagerly
2. SharingStarted.Lazily
3. SharingStarted.WhileSubscribed

# someFlow.first() 와 someFlow.value의 차이는
first()는 flow를 collect해서 가장 처음 emit된 값을 가져오고 즉시 종료한다.
즉 collect { it -> } 을 호출해서 첫번째 값 나오면 바로 종료된다.
value는 flow가 현재 메모리에 들고 있는 값을 바로 가져온다.
즉 기다려주지 않는다.


# uiState와 validUiState를 분리하는 이유.
미리 요약하자면, 순환 의존을 방지하려는 것이다.
입력값의 의해 uiState가 바뀌고,
uiState가 바뀔때마다 입력된 폼에 의한 data를 갱신하고,
data의 유효성을 체크해서, valid를 uiState에서 갱신해버리면,
순환되버린다.
이 문제를 해결하는 가장 단순한 방법은
valid와 ui를 나누는 것이다.
data의 유효성을 체크해서 validUiState가 갱신된다.
validUiState는 data를 갱신하지 않으므로, 순환되지 않는다.


# rememberScrollState..를 비롯한 수많은 remember들은 누가 다 일일히 만들어 놓은걸까?
그리고 이 함수들의 존재 기준은 뭘까?

# sealed class UiState를 지양해야 하는 이유.
sealed class는 분기 제어에는 유리하다.
여러 상태 타입을 깔끔하게 다룰 수 있다.
컴파일 단계에서 모든 분기를 처리했나 검사도 해준다.

UiState는 보통 뷰모델에서 단방향 흐름으로 만들게 된다.
이는 데이터 누적 갱신에 유리하며 보통 flow의 scan을 통해서 갱신하며, 기존 상태를 copy()하여 일부만 바꿔 새 상태로 발행한다.

문제는 이 두방식이 서로 충돌한다는 것이다.
sealed class는 타입 단위로 상태를 구분하므로, 전체 상태를 재생성하는 쪽으로 개발하게 되며,
이를 위해 현재 상태 타입 확인 -> 그 안에서 copy호출 하는 과정으로 진행하게된다.
이는 불필요한 유지보수를 낳게된다.

결국 reduce로직이 현재 상태 -> 다음 상태로 변환하는 로직이 아닌,
새로운 상태를 계속해서 발행해야 하는 상태가 되기 쉽다.

결국 가장 좋은 방법은 data class 하나로 UiState를 표현하는 것이다.
```kotlin
data class MyUiState(
    val isLoading: Boolean = false,
    val error: UiError? = null,
    val userList: List<User> = emptyList(),
)

fun MyUiState.reduce(action: MyAction): MyUiState {
    return when (action) {
        is MyAction.ShowLoading -> copy(isLoading = true)
        is MyAction.ShowError -> copy(isLoading = false, error = action.error)
        is MyAction.ShowUsers -> copy(isLoading = false, userList = action.users)
    }
}
```
사실상 sealed class UiState는 reduce를 무력화하는 패턴이라 안티패턴으로 봐도 무방할것 같다.


# derivedStateOf??
컴포즈에서 관찰중인 상태 또는 컴포저블 입력(파라미터)가 변경될때 마다 해당 컴포저블의 재구성이 실행된다.
때로는 상태객체나 입력이 UI가 업데이트 해야하는것보다 더 자주 변경되어 불필요한 재구성이 발생할 수도 있다.
컴포저블의 입력이 재구성보다 더 자주 변경되면, derivedStateOf를 사용해서 불필요한 재구성을 피할 수 있다.
또는 컴포저블 함수 내부에서 변경된 상태를 기반으로 새로운 상태를 만들때도 같이 사용한다.
```kotlin
@Composable
private fun TimeDraggableCardView(
   modifier: Modifier,
   startTime: LocalTime? = null,
   endTime: LocalTime? = null,
   slotItem: TimeSlotCardUiState,
   hourHeight: Dp,
   onClick: () -> Unit,
   onDragStop: (LocalTime, LocalTime) -> Unit
) {
    var startMinutes by remember(slotItem) {
        mutableIntStateOf(startTime?.asMinutes() ?: 0)
    }
    val draggedStartTime: LocalTime by remember(slotItem) {
        derivedStateOf {
            //내부에서 변경하는 startMinutes에서 파생된 결과를 재구성 기준으로 써야하므로, derivedStateOf 사용.
            if (startTime == null) {
                slotItem.startTime.minusMinutes(
                    ((LocalDateTimeUtil.DAY_MINUTES - startMinutes) % LocalDateTimeUtil.DAY_MINUTES).toLong()
                )
            } else {
                startMinutes.minutesToLocalTime()
            }
        }
    }
    // ...
}
```
위 코드의 주석에서 확인 가능하듯,
startMinutes는 함수 내부에서 변경이 가능하고,
이 변경된 값에 의해 계산된 값을 기준으로 리컴포지션할 값이 필요할 경우, derivedStateOf를 사용할 수 있다.


# composable함수가 만드는것.
Compose는 @Composable함수 호출 결과를 *SlotTable*로 저장하고, 
이 슬롯테이블을 UI트리를 재구성할때 참조하는 *스냅샷* 역할을 한다.


| 구분    | View 시스템                                  | Compose                                                |
|-------|-------------------------------------------|--------------------------------------------------------|
| 계층    | View 인스턴스 트리                              | LayoutNode 트리                                          |
| 상태 저장 | View 자체의 멤버 변수                            | Slot Table + CompositionLocal + State 객체               |
| 업데이트  | requestLayout(), invalidate()로 View 전체 갱신 | 상태 변경 → Recomposition → 필요한 노드만 갱신                     |
| 객체 수명 | View 인스턴스를 직접 만들고/파괴                      | Composable 호출 결과를 Slot Table이 관리, 필요할 때만 LayoutNode 갱신 |


compose는 기존 View처럼 무거운 Java/Kotlin객체를 매번 생성하진 않는다.
1. 함수 호출 → Slot Table 기록 
2. Slot Table → LayoutNode와 Modifier 트리 갱신 
3. 바뀐 부분만 diff해서 apply
때문에 UI갱신이 빠르고, 필요없는 객체를 생성하진 않는다.

# CompositionLocal
CompositionLocal compose에서 전역상태를 보관하기 위해 사용된다.
아래와 같이 쓸 수 있다.
```kotlin
val LocalUser = compositionLocalOf<User> { error("No user found!") }

@Composable
fun App(user: User) {
   CompositionLocalProvider(LocalUser provides user) {
      ProfileScreen() // 이 아래의 Composable들은 LocalUser로 user 객체를 꺼낼 수 있음
   }
}

@Composable
fun ProfileScreen() {
   val user = LocalUser.current
   Text("Hello, ${user.name}")
}
```
신중하게 써야 한다.
사실상 컴포즈에서 읽을 수 있는 전역변수 같은게 된다.
화면이 굉장히 많은 상태의 영향을 받게 되어 디버깅이 힘들어 질 수 있다.
컴포즈에서 대부분의 상태는 viewModel에서 발행한 uiState에서 처리하는게 가장 이상적이라고 생각한다.
그럼에도 불구하고 사용한다면, 상태가 뷰모델까지 전파될 필요가 없는 요소들을 CompositionLocal로 전역화한다.
예를 들면, LocalDensity를 사용해서 현재 DP를 픽셀로 변환할때 사용할 수 있다.
```kotlin
val density = LocalDensity.current
val hourHeightPx = density.run { hourHeight.toPx() }
```

# UiState에 uuid를 넣어도 될까?
항상 고민되는 부분이다.
일단 안된다는 생각을 정리하면,
uiState는 화면을 모델링한 데이터 클래스이다.
때문에 화면에 보여지는 요소가 아닌 도메인 데이터의 식별자는 넣는건,
화면 상태의 오염이라고 본다.
하지만, 화면은 인터렉션이 일어나는 곳이고, 인터렉션을 할때 동작인 uiIntent를 넣는것 까진 화면상태에 포함해도 될것 같은데..
문제는 ux를 드래그 하며 ux에서 발생한 값으로 intent를 발행해야 한다면,
uiState에 uuid라도 있어야 intent를 발행한다는 것이다.
물론 이 또한 오염을 피하는 방법이 있다.
뷰 모델에서 현재 데이터 상태를 들고 있고,(예를 들면 목록)
uiState에서는 목록의 인덱스만 가지고 있는 방법이 있을수 있다.
그러면 uiState는 오염되지 않고, 인터렉션에 필요한 uuid도 찾을 수 있지만,
intent의 동작이 복잡해진다는 문제가 있다.
특히 intent는 handleSideEffect와 관찰하여 상태를 갱신하는 두가지 동작으로 처리되는데,
intent의 동작을 할때마다 intent를 발행한 uiState의 식별자를 가지고, 도메인 식발자를 매칭하고 화면을 갱신하거나
비지니스 로직을 실행해야 한다는 것이다.
...일단 uiState에 들고 있자. 그냥 식별자이기도 하니깐..
