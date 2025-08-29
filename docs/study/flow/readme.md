# StateFlow 와 LiveData
둘은 비슷하다.  
UI에서 관찰(observe, collect)가능한 데이터 홀더 역할을 한다.  
LifecycleOwner와 연결해 데이터 변경 -> UI갱신 플로우를 만든다.  
메인스레드의 안정성 역시 제공한다.

### 다만 LiveData는...  
Android Framework에 의존된다.  
> 왠지 지금 드는 생각이, 도메인 모듈에서 쓸 수 없겠단 생각이 먼저 들었다.  

그리고 비동기 스트림 연산 함수가 제한적이다. map, switchMap정도만 지원한다.

> 솔직히 이런 상황이면 이건 안쓰는게 맞을것 같다.  
> 왜냐하면, 순수 모듈이어야 하는 도메인 로직과 결합에 문제가 있고,  
> 그 기능도 제한적이며,  
> 코틀린 비동기 처리 관련해서 강력한 Flow와 결합하기도 힘들다.

# Stream
연속적인 데이터 흐름을 말한다.  
즉 파이프라인을 따라 흘러가는 데이터라고 생각하면 된다.

# Cold Stream
collect(구독) 하기 전까지 실행되지 않는 스트림.

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
기본적으로 Cold Stream인 Flow와 달리, 이녀석은 Hot Stream(Hot Flow).
collect가 호출되지 않아도, 계속 방출되고 있음.

