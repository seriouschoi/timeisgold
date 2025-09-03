# Scratch board.
(임시 작성 보드.)

# 테스트 작성 시점에 대한 고민.

## 기능 개발과 테스트 작성 시점의 트레이드 오프
- 유즈케이스를 추가하는 순간이 테스트를 작성하기 가장 이상적이다.
    - 이때가 맥락을 가장 깊게 이해하고 있기 때문.
- 하지만, 기능 개발 흐름 도중 테스트 작성에 들어가면 **맥락 전환(Context Switching)** 으로 인해 생산성이 떨어지고, 작업이 단절되는 문제가 있다.
- 따라서, 실무에서는 기능 구현과 테스트 작성의 균형점을 찾는 것이 필요하다.

## 차선책: TODO 기반 방식
- 새로 작성한 유즈케이스나 함수에 // TODO test 형태로 마킹.
- 기능 개발 완료 후, 머지 직전에 테스트를 작성/보강.
- 그러나, 이 방식은 휴먼 에러로 인해 마킹 누락이나 방치가 발생할 수 있다.

## 최적해: 커버리지 도구 기반 방식
- 커버리지 도구(JaCoCo, Kover 등)를 활용해 자동으로 테스트 누락을 검출한다.
- TODO 주석에 의존하지 않고도 테스트 미작성 구간을 식별할 수 있다.
- CI에 연동하면, 테스트 커버리지가 줄어들거나 신규 코드가 테스트되지 않았을 때 자동으로 감지 가능하다.

## 결론.
> 테스트 작성 시점은 항상 **이상(코드 작성 직후)** 과 현실(맥락 전환 비용) 사이의 트레이드 오프다.  
> 따라서, 단기적으론 TODO 마킹, 장기적으론 커버리지 자동화 도입이 가장 효율적인 접근이다.


# Domain Service

## 정의
엔티티 하나에만 속하지 않는 비즈니스 규칙/정책을 담당하는 도메인 계층의 서비스.
- 특정 엔티티의 책임으로 넣으면 역할이 비대해지거나 부적절한 경우에 별도의 도메인 서비스로 추출한다.
- 도메인의 의미를 담은 연산을 표현하는 것이 목적.

## 특징
- 상태(state)를 직접 들고 있지 않고, *행위(behavior)* 만 가진다.
- 비즈니스 로직을 *Application Service(유즈케이스)* 가 아니라 도메인 계층에서 표현하게 해줌.

## 예시
TimeRoutine 요일 중복 검사
- TimeRoutine 엔티티 단일 책임으로 넣으면 과해짐.
- 여러 TimeRoutine과 DayOfWeek가 걸린 규칙 → 도메인 서비스 적합.

# hiltViewModel() in composable
## ViewModelStoreOwner와 NavBackStackEntry
- Compose Navigation에서는 각 NavBackStackEntry가 자체적으로 ViewModelStoreOwner(뷰모델 저장관리주체) 역할을 합니다.
- hiltViewModel(backStackEntry), 혹은 hiltViewModel() 을 호출하면, 
  - 그 backStackEntry 범위에서 ViewModel을 관리하게 됩니다.
- 따라서 NavBackStackEntry가 백스택에서 살아있는 동안 ViewModel 인스턴스는 재사용됩니다.
- 반대로, 해당 엔트리가 **pop**되면 ViewModelStore도 사라지고 → ViewModel은 dispose → 새로 진입하면 새 인스턴스가 생성됩니다.

## NavHost와의 관계
- NavHost는 실제로 NavBackStackEntry들을 보관하는 컨테이너입니다.
- 만약 동일한 NavHost 안에서 같은 route를 push/pop 하면, hiltViewModel()은 그 backStackEntry에 바인딩된 ViewModel을 재사용합니다.
- 하지만 다른 NavHost라면 ViewModelStoreOwner가 달라지므로, hiltViewModel()은 새로운 ViewModel을 생성합니다.

## 예시
```kotlin
NavHost(navController, startDestination = "home") {
    composable("home") { backStackEntry ->
        val vm: HomeViewModel = hiltViewModel(backStackEntry)
        HomeScreen(vm)
    }
    composable("detail/{id}") { backStackEntry ->
        val vm: DetailViewModel = hiltViewModel(backStackEntry)
        DetailScreen(vm)
    }
}
```
- home -> detail 이동 시: HomeViewModel은 여전히 유지됨 (backStackEntry 살아있음).
- detail -> popBackStack() 시: DetailViewModel은 파괴됨.
- 다시 detail로 navigate하면 -> 새로운 DetailViewModel 인스턴스 생성.

## 요약
- hiltViewModel()은 NavBackStackEntry 단위로 ViewModel을 제공.
- NavHost가 다르면 ViewModel도 별도 관리 → 새로 생성됨.
- 같은 NavHost 안에서는 backStackEntry 생존 여부에 따라 재사용/재생성 여부가 결정됨.

# hiltViewModel(backStackEntry) 와 hiltViewModel() 차이
인자로 아무 것도 주지 않으면, Compose는 **현재 Composition의 ViewModelStoreOwner**를 자동으로 찾는다.  
기본적으로는 현재 NavBackStackEntry가 Owner로 쓰입니다.  
그래서 NavHost 안의 composable 블록에서 그냥 hiltViewModel()을 호출하면,  
해당 route에 해당하는 NavBackStackEntry에 속한 ViewModel이 생성됩니다.

hiltViewModel(backStackEntry)는 명시적으로 NavBackStackEntry를 전달한다.  
자식 route에서 부모 route의 viewModel을 재사용하고 싶을때 사용한다.

예시)
```kotlin
composable("parent") { parentEntry ->
    val parentVM: ParentViewModel = hiltViewModel(parentEntry)

    NavHost(navController, startDestination = "child") {
        composable("child") { childEntry ->
            // childEntry에 속하면 childVM은 child 스코프
            val childVM: ChildViewModel = hiltViewModel(childEntry)

            // parentEntry를 명시적으로 지정하면 같은 ParentViewModel 재사용 가능
            val sameParentVM: ParentViewModel = hiltViewModel(parentEntry)
        }
    }
}
```

근데 이 경우는 피하는게 좋을것이다. 부모 뷰모델과 자식 뷰와 결합은 권하는 구조는 아니다.  
부모 뷰모델이 자식 뷰모델의 책임까지 가지면서, 잘못된 결합이 생길 우려가 높다.

# SavedStateHandle
뷰모델이 들고 잇는 키값 형태의 저장소.  
프로세스가 죽어도, 복원될때 가져올 수 있는 값들이 저장된다.  
즉 여기에 담는 값은 직렬화가 가능해야 하며,  
뷰모델이 다시 살아날때, 복원에 필요한 최소한의 키값만 들고 있는 것이 바람직 하다.

예시.  
특정 요일의 TimeRoutine의 편집 화면에서  
savedStateHandle에 routineUuid는 담지 않는게 옳다.  
해당 화면은 요일의 루틴이다. 만약 어떤 프로세스든 다른 루틴이 이 요일에 저장되었다면,  
프로세스가 죽었다 살아날때, 요일 기반으로 새로 저장된 루틴을 가져오는게 바람직하다.

# uiState.isLoading vs sealed class UiState { Loading … }

## 플래그 방식 (uiState.isLoading)
```kotlin
data class UiState(
    val title: String = "",
    val isLoading: Boolean = false,
)
```

- 장점: 단순하다. 기존 UiState에 boolean 하나만 추가하면 된다.
- 단점:
  - ViewModel에서 isLoading = false 처리를 깜빡하면 로딩이 영원히 끝나지 않는 UI가 남는다.
  - 여러 플래그(isLoading, isError, isEmpty …)가 쌓이면 상태 관리가 복잡해진다.

⸻

## 상태 전환 방식 (sealed class UiState)

```kotlin
sealed class UiState {
    object Loading : UiState()
    data class Content(val title: String) : UiState()
    data class Error(val message: String) : UiState()
}
```

- 장점:
  - 상태 전환이 명확하다. Loading → Content → Error 같은 흐름이 sealed class로 표현된다.
  - when(uiState)를 쓰면 모든 분기를 컴파일러가 강제하므로 누락 실수를 줄인다.
  - 관리해야 할 boolean 토글이 없다.
- 단점:
  - “로딩 중에도 기존 화면을 흐리게 보여주고 싶다” 같은 UX를 구현하기에는 다소 불편하다.

⸻

## 어느 쪽을 선택해야 하나?
- UX가 “로딩 중에 기존 데이터가 남아있어야 한다” → 플래그 방식(isLoading).
- UX가 “로딩 중엔 그냥 덮어버리면 된다” → 상태 전환 방식(sealed class UiState).

## 결론

우리는 실수를 방지하는 쪽을 선택해야 한다.  
로딩 플래그는 작은 실수로도 “계속 로딩이 남아있는 화면”을 만들 수 있다.  
그럴 바에는 UX를 다시 점검해서, 굳이 기존 입력 요소를 흐리게 보여줄 필요가 없다면 sealed class로 Loading을 별도 상태로 정의하는 것이 더 안전하다.


# rememberSaveable??

# @Upsert
room에서 insert or update를 해주는 어노테이션이다.  
편리해보이지만, conflict발생시, 기존 중복 row를 삭제하고,   
새 row를 추가하므로, 예상치 못한 덮어쓰는 일이 발생하므로,  
사용을 지양하는 것이 옳다고 본다.