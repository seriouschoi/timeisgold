# TimeSlotListPageViewModel refac


## watch time slot list 개선.
slotList의 success만 watch해서 목록 stateHolder를 갱신하고,
여러 상태를 fail만 watch해서 오류를 state를 표시하고,
여러 상태의 loading만 watch해서 로딩을 보여주는 방법도 괜찮을려나..?

이를 위해선, uiState의 내부를 좀더 세분화 해야할 수도 있겠네..
스크린 데이터,
스크린 오류,
스크린 로딩,

하단 항목 편집 창.
편집창 오류.
편집창 로딩.

근데 그렇게 만들면, 속성이 너무 복잡해지는것 같은데..
현재 스크린 상태에 데이터, 오류, 로딩 속성을 두고 있는데..
이걸...차라리 오류가 일어날 상태를 수신하고 있다가..
상태 홀더에 오류라고 던지는 구현이 더 낫겠는데..

음...지금 이 화면 기준으로..
슬롯 목록 오류 watch,
슬롯 목록 로딩 watch로 하나 만들고..
각각 스크린 상태 홀더에 스크린 오류 인텐트, 스크린 로딩 인텐트를 던지면 될것 같아.

말한거랑 약간 달라지긴 하네..
watchForScreenLoading,
watchForScreenError같은 개념으로 가야하나..

그리고 이들을 merge해서 하나의 watchForScreenLading같은걸로 합쳐서 관리할 수도 있고..
when(resultState) 블록의 크기도 줄이고..

줄이긴 했는데...

## 2중 랩핑 제거.
ResultState<DomainResult<SomeDomainVO>> 같은 구조로 만드니 2중 랩핑 처리하느라.. 코드가 개판이 난다..
이걸 `ResultState<SomeDomainVO>`로 바꿔야 한다.
...이걸 이제야 깨닫다니..

`ResultState`에 오류 코드를 포함할 수 있도록 개선이 필요해 보인다.

