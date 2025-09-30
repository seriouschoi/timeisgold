# 자정을 넘겨서 쪼갠 슬롯뷰의 터치 효과가 하나만 표시됨.
* [x] 선택 여부를 상태로 관리.

# 하단 선택 기준이 오동작이 일어남.
* [x] 드래그를 몇번 시도하고, 중단을 누르고 드래그 하면...카드 드래그가 아니라, 하단 확장됨.
  * PointerInputEventHandler가 slotHeight를 스냅샷으로 들고 있어서 생기는 문제.

# 불필요한 탭 이벤트가 자꾸 감지됨.
* 원인
  * [x] pointerInput을 두번 붙이고 있었음.

# pointInput을 한번만 붙게 만들었더니, 터치가 안됨.
* 원인
  * [x] pointerInput을 CardView설정하기 전에 붙이고 있었음.
* 해결
  * pointerInput은 사이즈가 정해지고 마지막에 붙이자.

