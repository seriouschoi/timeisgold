# Test

## Turbine.

Turbine은 Flow의 테스트를 제공하는 라이브러리.


## Turbine 주요 API
- awaitItem()
  - 다음 이벤트가 Item일 때 그 값을 반환하고, 그렇지 않으면 AssertionError 발생.
  - 대기했다가 쓰는 방식(풀 기반).
- awaitComplete()
  - Flow가 정상적으로 완료될 때까지 대기. 완료 이벤트가 아니면 AssertionError.
- awaitError()
  - Flow가 에러로 종료될 때, 그 Throwable을 반환.
- expectMostRecentItem()
  - 현재까지 수신된 이벤트 중에서 가장 최신 아이템만 반환. 이전 내용은 소모하지 않고 유지.
  - StateFlow처럼 중간값 스킵하고 최신값만 보고 싶을 때 유용.
- expectNoEvents()
  - 현재 큐에 남아 있는 이벤트가 없을 때 통과. 남아 있으면 AssertionError.
- skipItems(n)
  - 다음 n개의 아이템을 그냥 건너뜀.흐름에 방해 없이 스킵.
- cancelAndIgnoreRemainingEvents()
  - Flow 수집을 취소하고, 남아 있는 이벤트는 모두 무시. 빨리 테스트 끝내고 싶을 때.
- ensureAllEventsConsumed()
  - 테스트 블록 종료 시 자동으로 호출되며, 소모되지 않은 이벤트가 없는지 검증. 흐름 완전 소비 필수 여부 확인.


## advanceUntilIdle()
test환경에서 코루틴을 실행할때, runTest 블록을 사용한다.  
runTest블록안에서 실행되는 모든 코루틴은 TestScheduler에 등록된다.  
advanceUntilIdle()는 TestScheduler에 앞서 등록된 코루틴들이   
전부 완료될때까지 가상 시간을 흐르게하는 테스트용 함수이다.