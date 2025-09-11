# Flow 와 LiveData
둘은 비슷하다.  
UI에서 관찰(observe, collect)가능한 데이터 홀더 역할을 한다.  
LifecycleOwner와 연결해 데이터 변경 -> UI갱신 플로우를 만든다.  
메인스레드의 안정성 역시 제공한다.

### 다만 LiveData는...  
Android Framework에 의존된다.  
왠지 지금 드는 생각이, 도메인 모듈에서 쓸 수 없겠단 생각이 먼저 들었다.  

그리고 비동기 스트림 연산 함수가 제한적이다. map, switchMap정도만 지원한다.
솔직히 이런 상황이면 이건 안쓰는게 맞을것 같다.  
왜냐하면, 순수 모듈이어야 하는 도메인 로직과 결합에 문제가 있고,  
그 기능도 제한적이며,  
코틀린 비동기 처리 관련해서 강력한 Flow와 결합하기도 힘들다.

# Flow(Stream)
연속적인 데이터 흐름을 말한다.  
즉 파이프라인을 따라 흘러가는 데이터라고 생각하면 된다.

# Hot/Cold Flow(Stream)
- Hot Stream: 
  - 구독 시점에 최신 값을 바로 전달함.
  - 즉, 데이터 생산은 공유되고, 소비만 여러 collect에서 이뤄짐.
  - 예시: StateFlow, SharedFlow
  - 사용례: UiStateFlow, UiEventFlow
- Cold Stream: 
  - collect할 때마다 처음부터 실행됨.(데이터 생산이 구독자마다 별도로 시작)
  - 일반적인 Flow는 Cold Stream이다.
  - 사용례: myDao.getUsers()
    - collect 할때마다 쿼리를 실행하는 구조.

쉬운 이해
- hot stream.
  - 핫라인 전화기와 같다.
  - 수화기를 드는 순간 상시 대화중인 채널에 바로 합류한다.
- cold stream.
  - 전화라인을 기본적으로 비활성이며,
  - 수화기를 들면 상대방이 응답하고 대화(emit)이 시작된다.

# Flow
Flow는 코틀린에서 제공하는 비동기 데이터 콜드 스트림이고,  
순차적으로 값이 흘러가는 파이프라인을 다루기 위한 도구이다.  

```kotlin
fun numbers(): Flow<Int> = flow {
    for (i in 1..3) {
        delay(100)     // 비동기 안전하게 지연 가능
        emit(i)        // 값 흘려보내기
    }
}

// collect (구독)
numbers().collect { value ->
    println(value)
}
```
기본 구조는 위와 같다.  
numbers()는 100ms마다 순차적으로 1부터 3까지 방출하고,   
collect한 블록에서 1부터 3까지 순차적으로 수신한다.

- 그외 특징 
  - map, filter, take, flatMapLatest와 같은 변환 연산자가 있다.
  - 스트림을 조합/변환을 함수형으로 할수 있다.

*아래는 변환 연산자 사용예.*
```kotlin
flowOf(1, 2, 3)
    .map { it * 2 }            // 값 변환
    .filter { it > 2 }         // 조건 필터
    .onEach { println("값:$it") } // 중간 로깅
    .collect { println("최종:$it") }
```

Room + Flow를 사용할 수도 있다.
```kotlin
@Query("SELECT * FROM User WHERE id = :id")
fun getUser(id: String): Flow<User?>
```
Room내부적으로 DB변경을 감지해서 자동으로 Flow를 emit해준다.

# emit(data)
flow에 data를 발행.

# StateFlow
hot flow이며,
항상 최신 값을 갱신하고 있고, 
새로운 구독자는 마지막 '상태'를 바로 받는다.
즉 이 flow를 여러 곳에서 collect하면, 
나중에 collect한 곳에선 마지막 상태만 수신하게 된다.



