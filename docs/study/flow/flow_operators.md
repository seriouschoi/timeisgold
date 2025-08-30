# Flow 조합 및 변환.

기본 변환.
```kotlin
flowOf(1, 2, 3, 4, 5)
  .filter { it % 2 == 0 }      // 2, 4
  .map { it * 10 }             // 20, 40
  .onEach { log("중간 로깅: $it") }
  .collect { println(it) }     // 20, 40
```

최신 값 추적. (검색어가 바뀔 때마다 이전 검색 취소하고 최신만 유지)
```kotlin
val queryFlow: Flow<String> = searchTextChanges()   // TextField → Flow

queryFlow
  .debounce(300)
  .distinctUntilChanged()
  .flatMapLatest { query -> api.search(query) }     // Flow<List<Item>>
  .catch { emit(emptyList()) }
  .collect { render(it) }
```

다중 소스 조합.
```kotlin
val userFlow: Flow<User> = dao.user(userId)         // Room Flow
val settingsFlow: Flow<Settings> = settingsRepo.observe()

combine(userFlow, settingsFlow) { user, settings ->
  UiModel(user, settings.theme)
}.collect { ui -> render(ui) }
```

DB 반응형 체인
```kotlin
fun getTimeRoutineDetail(week: DayOfWeek): Flow<TimeRoutineDetailData?> {
  val dao = appDatabase.TimeRoutineRelationDao()
  return dao.latestUuidByDayOfWeek(week)            // Flow<String?>
    .distinctUntilChanged()
    .flatMapLatest { uuid ->
      if (uuid == null) flowOf(null)
      else dao.get(uuid)                             // Flow<TimeRoutineRelation?>
              .map { it?.toDomain() }
    }
}
```

null 제거.
```kotlin
dao.latestUuidByDayOfWeek(week)        // Flow<String?>
    .mapNotNull { it }                   // Flow<String>
    .flatMapLatest { uuid -> dao.get(uuid) }
    .map { it.toDomain() }               // 이제 null 아님
```

에러 처리.
```kotlin
api.stream()
  .retryWhen { e, attempt ->
    if (e is IOException && attempt < 3) {
      delay(500L * (attempt + 1))     // 지수 백오프
      true
    } else false
  }
  .catch { e -> emit(FallbackValue) }
  .collect { consume(it) }
```

백프레셔 최적화: buffer / conflate  
(프로듀서가 빠르고 컨슈머가 느릴 때 병렬 완충 또는 최신값만 유지.)
```kotlin
sourceFlow
  .buffer(capacity = 64)    // emit 측을 더 안 막음(버퍼링)
  .collect { slowConsume(it) }

// or 최신값만 유지(중간값 스킵)
sourceFlow
  .conflate()
  .collect { slowConsume(it) }
```

스로틀/샘플링(입력 폭주 억제)
```kotlin
searchInputFlow
  .debounce(300)                 // 입력 멈춘 뒤 300ms 후 발행

rapidSensorFlow
  .sample(1000)                  // 1초마다 최신값 한 번만
  .collect { updateGraph(it) }
```

누적/상태 만들기
```kotlin
events.scan(0) { acc, ev -> acc + ev.delta }   // Flow<Int> 누적합
  .collect { total -> render(total) }
```

시작/종료 훅
```kotlin
loadFlow()
  .onStart { showLoading() }
  .onCompletion { hideLoading() }
  .collect { render(it) }
```

ViewModel에서 StateFlow로 노출(UI에 “마지막 상태”를 보존/공유.)
```kotlin
class Vm(
  private val repo: Repo,
  private val scope: CoroutineScope
) {
  val uiState: StateFlow<UiState> =
    repo.observeData()
      .map { UiState(data = it) }
      .stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Empty
      )
}
```

병렬 처리: flatMapMerge / 순서 보장: flatMapConcat
```kotlin
// 각각의 요청을 병렬로 처리하고 섞어서 흘려보냄(빠른 것 먼저 도착)
ids.asFlow()
  .flatMapMerge(concurrency = 4) { id -> api.load(id) }
  .collect { consume(it) }

// 순서를 보장하며 차례로(직렬) 처리
ids.asFlow()
  .flatMapConcat { id -> api.load(id) }
  .collect { consume(it) }
```

리소스 안전: callbackFlow (콜백 → Flow)
```kotlin
fun bluetoothEvents(): Flow<Event> = callbackFlow {
  val listener = object : BtListener {
    override fun onEvent(e: Event) { trySend(e) }
  }
  bt.addListener(listener)
  awaitClose { bt.removeListener(listener) }     // 종료 시 정리
}
```

## 적용 가이드
적용 가이드(요점)
- 선택 스위칭: flatMapLatest
- 여러 데이터 결합: combine
- 입력 폭주 제어: debounce/sample
- UI 상태 보존/공유: stateIn/shareIn
- DB 반응형 상세 조회: “키 Flow → flatMapLatest → 상세 Flow”
- 속도 미스매치: buffer/conflate