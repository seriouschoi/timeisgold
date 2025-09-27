

# composition memory

compose가 ui트리를 함수 호출(@Composable)로 만들지만,  
매번 전체를 새로 그리진 않음.    
이전에 그린 UI트리와 현재 함수 호출 결과를 비교해서, 바뀐곳만 새로 그림.  
이때 각 composable호출 지점에 대응되는 데이터를 저장하는 저장소가 필요한데,    
이를 컴포지션 메모리, 내부적으로는 슬롯테이블 이라고 한다.

# remember

리컴포지션때 유지할 값.
프로세스가 재시작되도 유지할 값은 rememberSaveable에 저장.
슬롯 테이블에 저장한다. 그 말인즉, remember로 감싼 값이 바뀌면,
리컴포지션이 일어난다.

```kotlin
@Composable
fun Test() {
    // ...
    val dayOfWeekListFlow = remember(viewModel) {
        viewModel.uiState.map { it.dayOfWeekList }.distinctUntilChanged()
    }
    // ...
}
```

~~위 함수에서 슬롯테이블에 viewModel키로 uiState의 dayOfWeekList의 flow를 캐시함.~~  
viewModel은 키가 아니다.
remember 블록을 다시 실행할지 결정하는 트리거이다.
- 같은 viewModel 인스턴스면, 이전에 슬롯에 저장된 Flow를 재사용한다.
- 다른 인스턴스로 바뀌면, 블록이 재실행된다. -> 새 Flow를 만들고, 슬롯 값이 교체된다.
> 근데 이건 마치 키처럼 보이는데..  
> remember 함수를 열어봐도 파라미터 명이 key이다.  
> 근데 슬롯테이블에 viewModel로 접근할 수 있는건 아니니깐, 
> 개념적으로는 트리거가 맞다.  


# recomposition
compose가 ui트리를 함수 호출(@Composable)로 만들지만,  
매번 전체를 새로 그리진 않음.  
이전에 그린 UI트리와 현재 함수 호출 파라미터를 비교해서, 바뀐곳만 새로 그린다.  
이 과정을 리컴포지션이라 한다.

> @Composable 함수를 다시 호출하는 과정에서,  
> 슬롯테이블(컴포지션 메모리)를 참고해 바뀐 부분만 갱신하는 과정을 말한다.

```kotlin
@Composable
fun Test() {
    // ...
    val dayOfWeekListFlow = remember(viewModel) {
        viewModel.uiState.map { it.dayOfWeekList }.distinctUntilChanged()
    }
    // ...
}
```
그렇다면 위의 코드에서 dayOfWeekList가 바뀌면, 리컴포지션이 일어나는가?


# collectAsState

collectAsState는 flow를 구독(collect)해서 수신된 값을 Compose에서 관찰가능한 State로 바꿔준다.  

```kotlin
val dayOfWeekList by dayOfWeekListFlow.collectAsState(initial = emptyList())
```

# by
```collectAsState()```와 같은 함수를 사용하면,  
리턴 타입은 State<T>이고 상태값은 ```state.value```를 통해 확인할 수 있다.  

by는 kotlin의 프로퍼티 위임 문법이다.
```kotlin
val dayOfWeekList by dayOfWeekListFlow.collectAsState(initial = emptyList())
```
즉 위와 같은 코드가 있을때, 
by를 쓰지 않으면, ```dayOfWeekList.value```와 같은 형태로 접근해야 하지만,
by를 쓰면 ```dayOfWeekList```로 바로 접근이 가능하다.

