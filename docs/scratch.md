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

# build.gradle과 build.gradle.kts 차이???

# Compose에서 쓰는 Unit은 뭐지?

# focus??

# someFlow.first() 와 someFlow.value의 차이는??