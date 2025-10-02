# 드래그 스왑버그.

드래그로 인해 스왑이 되서 화면이 갱신이 되도, 
드래그 중인 이벤트가 들고 있는 아이템을 기준으로 계속 intent를 발행하고 있어서,
드래그가 정상적으로 이뤄지지 않는다.

일단 목표는 연속적으로 순서를 주욱 바꿀 수 있는 드래그이다.

스왑이 되면 컴포즈에 있는 currentSlot을 갱신하여야 한다.
하지만 스왑은 뷰모델에서 처리하고 있다.
즉 뷰모델에서 스왑이 된걸 컴포즈에서 알아야 한다.
이를 위해 뷰모델에서 이벤트를 발행하고,
컴포즈에서 이벤트를 확인하면, currentSlot을 갱신해보자...
[ ] 이벤트를 정의하고, 컴포즈에서 이벤트를 받아 currentSlot갱신하기.

UpdateTime Intent에 의한 uiState reduce에서 구현을 하고 있었는데..
이걸 구현하기 위해선, 이를 뷰모델 안에서 sideEffect로 처리해야 한다.
[x] Update Time을 reduce -> sideEffect로 변경.

어...그러면.. sideEffect로 uiState를 갱신한다고 치자..
그러면.. uiState는
routineCompositionFlow + _intent를 관찰하면서 uiState를 만드는데..
sideEffect로 uiState를 갱신하려면..
updateSlotListState를 UiPreState.UpdateSlotList 콜드스트림으로 만들고,
해당 콜드 스트림을 갱신해서, uiState가 갱신되게 하는건가..
[x] UiPreState.UpdateSlotList 콜드스트림 만들기.