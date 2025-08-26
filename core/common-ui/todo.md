공통 문자열 리소스 처리를 아래와 같이 한다.
```kotlin
sealed class UiText {
    data class Res(@StringRes val id: Int, val args: List<Any> = emptyList()) : UiText()
    data class Raw(val value: String) : UiText()
}
```