# TimeRoutinePager 개선 메모

## 문제
현재 ViewModel은 여러 상태 변화가 있을 때마다 usecase.apply()를 호출함.  
이 때문에 uiState의 임시 상태까지 apply가 실행됨.  
debounce 문제가 아니라, 의도되지 않은 apply 호출 자체를 막아야 함.

그래서 "사용자 의도(Intent)가 있을 때만 apply"로 방향을 잡았는데,  
ViewModel Intent가 StateHolder Intent를 감싸는 형태라 구조가 오염됨.  
즉, ViewModel Intent가 불필요하게 섞여버리는 상황.

---

## Intent 흐름 문제
AStateComposable에서 AStateIntent 발행  
-> PageComposable이 이걸 받아 PageIntent로 변환  
-> PageViewModel이 PageIntent를 받아 처리  
-> 필요하면 StateHolder에도 전달

결과적으로 사용자 이벤트 하나로 Intent 타입 변환이 두 번 일어남.  
매핑 로직이 여러 계층에 흩어지고 유지보수가 어려워짐.

---

## 정리: Intent는 ViewModel 계층에만 둔다
구조는 아래처럼 정리한다.

### StateComposable
- State로 화면을 그림
- 이벤트는 람다로 밖에 전달
- Intent 없음

### ScreenComposable
- 하위 컴포저블 콜백을 받아 ScreenIntent로 변환
- ViewModel에 전달

### ScreenViewModel
- ScreenIntent만 처리
- uiState 갱신
- 필요하면 StateHolder의 명령형 메서드 호출

### StateHolder
- Intent 없음
- 명령형 메서드만 가짐 (updateTitle, selectDay 등)

결론: Intent는 ViewModel의 API로만 유지하고,  
Composable이나 StateHolder로 퍼뜨리지 않는다.

---

## TimeRoutinePagerViewModel의 apply 문제
watchRoutineEdit에서 사용자 의도만 보고 apply를 호출하려면,  
먼저 Intent 구조를 위처럼 단순하게 정리해야 함.  
그 다음에 어떤 Intent가 apply를 트리거할지 명확히 정의하면 됨.

---

## Intent 기반 제어가 맞는가
Intent를 이용해 apply 타이밍을 제어하면 흐름이 이상해짐.

1. Intent를 combine해서 apply  
-> combine 내부에서 Intent 기반으로 state를 다시 읽어야 함  
-> 구조가 불명확해짐

2. Intent는 트리거만 하고, apply에 필요한 데이터는 state에서 읽음  
-> 조건과 데이터가 분리됨  
-> 흐름이 어지러워짐

결국 Intent로 문제를 우회하려는 것처럼 됨.  
실제 문제는 apply 타이밍이 아니라 상태 구조일 가능성이 큼.

---

## 결론
1. Intent는 ViewModel에만 둔다
2. Composable은 State와 람다만 가진다
3. StateHolder는 명령형 메서드만 가진다
4. apply 타이밍 문제는 Intent 필터링이 아니라 상태 구조 재정비로 해결해야 한다