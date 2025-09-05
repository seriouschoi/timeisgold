# TODOs

- [ ] uuid생성 규칙 일원화를 위해 도메인에 접근자 정의하기.
  - UUID.randomUUID().toString() 이걸 일일히 하다보면, 다른곳에서 다른 규칙을 쓸 우려가 있음.
- [ ] TimeRoutineRepositoryPort가 너무 큼. Composition과 Routine을 나눠야 함.
- [ ] 현재까지 만들어진 모든 뷰모델을 SavedStateHandle을 사용하여 초기화할 값 전달.
- [ ] 테스트 커버리지 도구 도입.


## Routine Page.
- 상단
  - [ ] 타임 루틴 이름, 요일
  - [ ] 루틴이 없을 때는 요일만 표시.

- 컨텐츠
  - [ ] 현재 요일의 타임 루틴 목록.
    - [ ] 타임루틴을 Flow로 가져오자.
    - 타임루틴을 표시하기 위해선,
    - [ ] 00:00~24:00 시간표가 있어야 하며, 그안에 타임슬롯을 출력해야 한다.
  - [ ] 루틴이 없을경우, 오늘은 루틴이 없습니다. 루틴을 만들까요? 라는 메시지 출력.

- ~~하단~~
  - ~~요일 탭바.~~
    - 매테리얼 가이드에서 탭바는 5개까지만 권장. 요일탭바는 구현하지 않음.
  - ~~요일 스크롤러.~~
    - ~~탭바 대신 seeker로 구현.~~
    - 타임 루틴을 탐색하면서 볼일은 없음.
    - 다만 전날,다음날 루틴이 조금 보이면 좋음.


## TimeSlot Tag.
- [ ] 타임슬롯에 태그 추가. 추후 리포트제공시 태그 기반으로 수행률 확인.
  - TimeSlotTag 추가.

## TimeSlot Memo
- [ ] 수행 기록제공을 할때, 사용자가 메모를 남기는 기능. 
  - 사진과 함께 다이어리 처럼 남기는 컨텐츠 제작도구 형태.

## github action.
- [x] android-connect에 적용된 mac 환경 제거.


## 개인용/공개용 앱으로 빌드 구성 분리.
- 목적: 클라우드 기능을 서비스로 제공할 경우, 지속적인 비용 부담. 개인용으로만 사용.
- [ ] :app 모듈을 :app-public, :app-personal 모듈로 분리.
- [ ] 기존 :app 모듈의 내용을 :presentation 모듈로 분리.
- [ ] :app-public, :app-personal은 각 빌드 구성에 필요한 모듈만 포함.
  - :app-public은 data-room-adapter만 제공.
  - :app-personal은 data-cloud-adapter를 제공.


