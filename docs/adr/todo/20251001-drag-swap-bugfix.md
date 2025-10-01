# 드래그 스왑버그.
자정에 걸친 상태에서 스왑 문제 발생.
timeslot order change.
intentItem=03:00~04:00,
overlapItem=22:00~03:00

timeslot order changed.
newIntentItem=02:00~03:00,
newOverlapItem=03:00~08:00

드래그 방향은 아래에서 위로.
기대되는 값.
timeslot order changed.
newIntentItem=22:00~23:00,
newOverlapItem=23:00~04:00

```
timeslot order change.
intentItem=03:03~04:03,
overlapItem=21:00~03:00,
intentItem.startMinutesOfDay=183,
overlapItem.startMinutesOfDay=1260

timeslot order changed.
newIntentItem=02:00~03:00,
newOverlapItem=03:03~09:03
```
아래에서 위로 올라가고 있고, 자정이 넘어가는 이벤트는, 마이너스 startMinutes를 쓰고 있어서 괜찮을 줄 알았지만..
상단은 마이너스 시작 시간, 하단은 오버 타임 종료시간이 있다보니..
하단의 오버타임이 시작시간은 인텐트의 시작시간보다 뒤에 있다.

**overlapItem을 찾을때... time으로 변환하지 않고 minutes를 기준으로.. 
으로 하면...실시간드래그할때, splitMidnightOver 기준이 무너짐.**