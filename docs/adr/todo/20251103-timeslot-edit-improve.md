# 타임 슬롯 편집 고도화.

## 슬롯 편집뷰가 표시된 상태에서, 목록 동작 변경.
- 선택된 슬롯만 드래그 가능. 나머지 요소는 드래그 불가.
- 스크롤: 목록 스크롤.

## 타임 슬롯 에딧 뷰의 시간 선택 범위 제약설정하기.
- 슬롯의 전후 시간 까지만 허용.
- 지금 생각해보니 드래그해서 바꾸기보다..이걸 먼저 만들걸 그랬네...
- 엔드 타임이 시작 시간보다 작게 설정 불가.
- 시작 시간이 엔드타임보다 늦게 설정 불가..?
  - 이거.. 자정 넘어가는거 설정하려면 허락 해야 할듯.

자정 넘어가는걸 감안해서 제약해야 하네..

일단..
stateholder쓰고 watch메소드를 쓰는 패턴이 되면서 뭔가 단방향이 깨진것 같은데..

단방향 데이터 흐름좀 정리해보자..

TimeSlotListPageViewModel기준..

1. TimeSlotListPageViewModel.load(dayOfWeek: DayOfWeek)
2. dayOfWeekFlow
3. timeSlotList
4. watchSlotList
