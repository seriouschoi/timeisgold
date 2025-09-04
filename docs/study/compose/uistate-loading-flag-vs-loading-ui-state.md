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